package com.juratempest.ms_pagos.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.juratempest.ms_pagos.client.FidelizacionClient;
import com.juratempest.ms_pagos.client.MaquinaClient;
import com.juratempest.ms_pagos.client.NotificacionClient;
import com.juratempest.ms_pagos.client.PromocionClient;
import com.juratempest.ms_pagos.client.ReservaClient;
import com.juratempest.ms_pagos.dto.CrearFidelizacionRequestDTO;
import com.juratempest.ms_pagos.dto.CrearNotificacionRequestDTO;
import com.juratempest.ms_pagos.dto.MaquinaResponseDTO;
import com.juratempest.ms_pagos.dto.PagoDTO;
import com.juratempest.ms_pagos.dto.PromocionResponseDTO;
import com.juratempest.ms_pagos.dto.ReservaResponseDTO;
import com.juratempest.ms_pagos.exception.BadRequestException;
import com.juratempest.ms_pagos.exception.ResourceNotFoundException;
import com.juratempest.ms_pagos.model.Pago;
import com.juratempest.ms_pagos.repository.PagoRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PagoService {

   private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_APROBADO = "APROBADO";
    private static final String ESTADO_RECHAZADO = "RECHAZADO";
    private static final String ESTADO_ANULADO = "ANULADO";
    private static final int IVA_PORCENTAJE = 19;

    private final PagoRepository repository;
    private final ReservaClient reservaClient;
    private final MaquinaClient maquinaClient;
    private final PromocionClient promocionClient;
    private final FidelizacionClient fidelizacionClient;
    private final NotificacionClient notificacionClient;

    public PagoService(
            PagoRepository repository,
            ReservaClient reservaClient,
            MaquinaClient maquinaClient,
            PromocionClient promocionClient,
            FidelizacionClient fidelizacionClient,
            NotificacionClient notificacionClient) {
        this.repository = repository;
        this.reservaClient = reservaClient;
        this.maquinaClient = maquinaClient;
        this.promocionClient = promocionClient;
        this.fidelizacionClient = fidelizacionClient;
        this.notificacionClient = notificacionClient;
    }

    public List<PagoDTO> listar() {
        log.info("Listando pagos");
        return repository.findAll().stream().map(PagoDTO::fromModel).toList();
    }

    public PagoDTO buscarPorId(Long id) {
        log.info("Buscando pago id={}", id);
        return PagoDTO.fromModel(obtenerPago(id));
    }

    public List<PagoDTO> buscarPorUsuario(Long usuarioId) {
        validarId(usuarioId, "El usuario es obligatorio");
        return repository.findByUsuarioId(usuarioId).stream().map(PagoDTO::fromModel).toList();
    }

    public List<PagoDTO> buscarPorReserva(Long reservaId) {
        validarId(reservaId, "La reserva es obligatoria");
        return repository.findByReservaId(reservaId).stream().map(PagoDTO::fromModel).toList();
    }

    public List<PagoDTO> buscarPorEstado(String estado) {
        estado = normalizarEstado(estado);
        return repository.findByEstado(estado).stream().map(PagoDTO::fromModel).toList();
    }

    public List<PagoDTO> buscarPorMetodoPago(String metodoPago) {
        metodoPago = normalizarMetodoPago(metodoPago);
        return repository.findByMetodoPago(metodoPago).stream().map(PagoDTO::fromModel).toList();
    }

    public long totalPagos() {
        return repository.count();
    }

    public PagoDTO crear(PagoDTO dto) {
        log.info("Creando pago reservaId={} usuarioId={}", dto != null ? dto.getReservaId() : null, dto != null ? dto.getUsuarioId() : null);
        validarDatos(dto);

        ReservaResponseDTO reserva = reservaClient.buscarPorId(dto.getReservaId());
        validarReserva(reserva, dto);
        validarPagoAprobadoDuplicado(dto.getReservaId());

        MaquinaResponseDTO maquina = maquinaClient.buscarPorId(reserva.getMaquinaId());
        validarMaquina(maquina);

        Pago pago = Pago.builder()
                .usuarioId(dto.getUsuarioId())
                .reservaId(dto.getReservaId())
                .promocionId(dto.getPromocionId())
                .metodoPago(normalizarMetodoPago(dto.getMetodoPago()))
                .estado(ESTADO_PENDIENTE)
                .fechaCreacion(LocalDateTime.now())
                .build();

        calcularMontos(pago, maquina);
        Pago guardado = repository.save(pago);
        notificarResultadoPago(guardado, "Pago pendiente", "Tu pago fue registrado y queda pendiente de aprobacion");
        return PagoDTO.fromModel(guardado);
    }

    public PagoDTO actualizar(Long id, PagoDTO dto) {
        log.info("Actualizando pago id={}", id);
        Pago pago = obtenerPago(id);
        validarDatos(dto);
        validarEditable(pago);

        ReservaResponseDTO reserva = reservaClient.buscarPorId(dto.getReservaId());
        validarReserva(reserva, dto);
        MaquinaResponseDTO maquina = maquinaClient.buscarPorId(reserva.getMaquinaId());
        validarMaquina(maquina);

        pago.setUsuarioId(dto.getUsuarioId());
        pago.setReservaId(dto.getReservaId());
        pago.setPromocionId(dto.getPromocionId());
        pago.setMetodoPago(normalizarMetodoPago(dto.getMetodoPago()));
        calcularMontos(pago, maquina);

        return PagoDTO.fromModel(repository.save(pago));
    }

    public PagoDTO aprobar(Long id) {
        log.info("Aprobando pago id={}", id);
        Pago pago = obtenerPago(id);
        if (!ESTADO_PENDIENTE.equals(pago.getEstado())) {
            throw new BadRequestException("Solo se pueden aprobar pagos pendientes");
        }

        validarPagoAprobadoDuplicadoAlActualizar(pago.getReservaId(), pago.getId());
        pago.setEstado(ESTADO_APROBADO);
        pago.setFechaPago(LocalDateTime.now());
        Pago guardado = repository.save(pago);

        registrarPuntosPorPago(guardado);
        notificarResultadoPago(guardado, "Pago aprobado", "Tu pago fue aprobado correctamente");
        return PagoDTO.fromModel(guardado);
    }

    public PagoDTO rechazar(Long id) {
        log.info("Rechazando pago id={}", id);
        Pago pago = obtenerPago(id);
        if (!ESTADO_PENDIENTE.equals(pago.getEstado())) {
            throw new BadRequestException("Solo se pueden rechazar pagos pendientes");
        }

        pago.setEstado(ESTADO_RECHAZADO);
        Pago guardado = repository.save(pago);
        notificarResultadoPago(guardado, "Pago rechazado", "Tu pago fue rechazado");
        return PagoDTO.fromModel(guardado);
    }

    public PagoDTO anular(Long id) {
        log.info("Anulando pago id={}", id);
        Pago pago = obtenerPago(id);
        if (ESTADO_APROBADO.equals(pago.getEstado())) {
            throw new BadRequestException("No se puede anular un pago aprobado");
        }
        if (ESTADO_ANULADO.equals(pago.getEstado())) {
            throw new BadRequestException("El pago ya esta anulado");
        }

        pago.setEstado(ESTADO_ANULADO);
        Pago guardado = repository.save(pago);
        notificarResultadoPago(guardado, "Pago anulado", "Tu pago fue anulado");
        return PagoDTO.fromModel(guardado);
    }

    public void eliminar(Long id) {
        Pago pago = obtenerPago(id);
        if (ESTADO_APROBADO.equals(pago.getEstado())) {
            throw new BadRequestException("No se puede eliminar un pago aprobado");
        }
        repository.deleteById(id);
    }

    //Calculos matematicos
    public int calcularIva(int subtotal){
        if (subtotal < 0){
            throw new IllegalArgumentException("El subtotal no puede ser negativo");
        }
        int iva = subtotal * IVA_PORCENTAJE / 100;
        return iva;
    }

    public int calcularMontoFinal(int neto, int descuento){
        if (neto < 0 ){
            throw new IllegalArgumentException("El valor neto no puede ser negativo");
        }
        if (descuento < 0){
            throw new IllegalArgumentException("El descuento no puede ser negativo");
        }
        if (descuento > neto){
            throw new IllegalArgumentException("El descuento no puede ser mayor al valor neto");
        }

        int subtotal = neto - descuento;
        return subtotal + calcularIva(subtotal);
    }

    //METODOS PRIVADOS

    private Pago obtenerPago(Long id) {
        validarId(id, "El id del pago es obligatorio");
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con id " + id));
    }

    private void validarDatos(PagoDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Los datos del pago son obligatorios");
        }
        validarId(dto.getUsuarioId(), "El usuario es obligatorio");
        validarId(dto.getReservaId(), "La reserva es obligatoria");
        normalizarMetodoPago(dto.getMetodoPago());
    }

    private void validarReserva(ReservaResponseDTO reserva, PagoDTO dto) {
        if (reserva == null || reserva.getId() == null) {
            throw new ResourceNotFoundException("La reserva no existe");
        }
        if (!dto.getUsuarioId().equals(reserva.getUsuarioId())) {
            throw new BadRequestException("La reserva no pertenece al usuario informado");
        }
        if (reserva.getEstado() != null && "CANCELADA".equalsIgnoreCase(reserva.getEstado())) {
            throw new BadRequestException("No se puede pagar una reserva cancelada");
        }
    }

    private void validarMaquina(MaquinaResponseDTO maquina) {
        if (maquina == null || maquina.getId() == null) {
            throw new ResourceNotFoundException("La maquina asociada a la reserva no existe");
        }
        if (maquina.getCostoPorBloque() == null || maquina.getCostoPorBloque() <= 0) {
            throw new BadRequestException("La maquina no tiene costo por bloque valido");
        }
    }

    private void validarEditable(Pago pago) {
        if (!ESTADO_PENDIENTE.equals(pago.getEstado())) {
            throw new BadRequestException("Solo se pueden actualizar pagos pendientes");
        }
    }

    private void validarPagoAprobadoDuplicado(Long reservaId) {
        if (repository.existsByReservaIdAndEstado(reservaId, ESTADO_APROBADO)) {
            throw new BadRequestException("Ya existe un pago aprobado para esta reserva");
        }
    }

    private void validarPagoAprobadoDuplicadoAlActualizar(Long reservaId, Long pagoId) {
        if (repository.existsByReservaIdAndEstadoAndIdNot(reservaId, ESTADO_APROBADO, pagoId)) {
            throw new BadRequestException("Ya existe otro pago aprobado para esta reserva");
        }
    }

    private void calcularMontos(Pago pago, MaquinaResponseDTO maquina) {
        Integer valorNeto = maquina.getCostoPorBloque();
        Integer descuento = calcularDescuento(valorNeto, pago.getPromocionId());
        Integer subtotal = valorNeto - descuento;

        pago.setValorNeto(valorNeto);
        pago.setMontoDescuento(descuento);
        pago.setIva(calcularIva(subtotal));
        pago.setMontoFinal(calcularMontoFinal(valorNeto, descuento));
    }

    private int calcularDescuento(Integer valorNeto, Long promocionId) {
        if (promocionId == null) {
            return 0;
        }

        PromocionResponseDTO promocion = promocionClient.buscarPorId(promocionId);
        validarPromocion(promocion);
        return Math.round(valorNeto * promocion.getPorcentajeDescuento() / 100);
    }

    private void validarPromocion(PromocionResponseDTO promocion) {
        if (promocion == null || promocion.getId() == null) {
            throw new ResourceNotFoundException("La promocion no existe");
        }
        if (!Boolean.TRUE.equals(promocion.getActiva())) {
            throw new BadRequestException("La promocion no esta activa");
        }
        if (promocion.getPorcentajeDescuento() == null || promocion.getPorcentajeDescuento() < 1 || promocion.getPorcentajeDescuento() > 100) {
            throw new BadRequestException("La promocion tiene un porcentaje invalido");
        }

        LocalDate hoy = LocalDate.now();
        if (promocion.getFechaInicio() != null && hoy.isBefore(promocion.getFechaInicio())) {
            throw new BadRequestException("La promocion aun no esta vigente");
        }
        if (promocion.getFechaFin() != null && hoy.isAfter(promocion.getFechaFin())) {
            throw new BadRequestException("La promocion ya expiro");
        }
    }

    private void registrarPuntosPorPago(Pago pago) {
        int puntos = Math.max(1, pago.getMontoFinal() / 1000);
        fidelizacionClient.registrarPuntos(CrearFidelizacionRequestDTO.builder()
                .usuarioId(pago.getUsuarioId())
                .puntos(puntos)
                .descripcion("Puntos por pago aprobado id " + pago.getId())
                .build());
    }

    private void notificarResultadoPago(Pago pago, String titulo, String mensaje) {
        notificacionClient.crearNotificacion(CrearNotificacionRequestDTO.builder()
                .usuarioId(pago.getUsuarioId())
                .titulo(titulo)
                .mensaje(mensaje)
                .tipo("PAGO")
                .canal("SISTEMA")
                .build());
    }

    private void validarId(Long id, String mensaje) {
        if (id == null) {
            throw new BadRequestException(mensaje);
        }
    }

    //normaliza estados para ahorrar codigo en los metodos publicos.
    private String normalizarEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            throw new BadRequestException("El estado es obligatorio");
        }
        estado = estado.trim().toUpperCase();
        if (!List.of(ESTADO_PENDIENTE, ESTADO_APROBADO, ESTADO_RECHAZADO, ESTADO_ANULADO).contains(estado)) {
            throw new BadRequestException("Estado no valido");
        }
        return estado;
    }

    private String normalizarMetodoPago(String metodoPago) {
        if (metodoPago == null || metodoPago.isBlank()) {
            throw new BadRequestException("El metodo de pago es obligatorio");
        }
        metodoPago = metodoPago.trim().toUpperCase();
        if (!List.of("EFECTIVO", "DEBITO", "CREDITO", "TRANSFERENCIA").contains(metodoPago)) {
            throw new BadRequestException("Metodo de pago no valido");
        }
        return metodoPago;
    }
}
