package com.juratempest.ms_promociones.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.juratempest.ms_promociones.dto.PromocionDTO;
import com.juratempest.ms_promociones.dto.ValidarPromocionRequestDTO;
import com.juratempest.ms_promociones.dto.ValidarPromocionResponseDTO;
import com.juratempest.ms_promociones.exception.BadRequestException;
import com.juratempest.ms_promociones.exception.ResourceNotFoundException;
import com.juratempest.ms_promociones.model.Promocion;
import com.juratempest.ms_promociones.repository.PromocionRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PromocionService {

    private final PromocionRepository repository;

    public PromocionService(PromocionRepository repository) {
        this.repository = repository;
    }

    public List<PromocionDTO> listar() {
        log.info("Listando promociones");
        return repository.findAll().stream().map(PromocionDTO::fromModel).toList();
    }

    public PromocionDTO buscarPorId(Long id) {
        log.info("Buscando promocion id={}", id);
        return PromocionDTO.fromModel(obtenerPromocion(id));
    }

    public PromocionDTO buscarPorCodigo(String codigo) {
        String codigoNormalizado = normalizarCodigo(codigo);
        log.info("Buscando promocion codigo={}", codigoNormalizado);
        return PromocionDTO.fromModel(repository.findByCodigo(codigoNormalizado)
                .orElseThrow(() -> new ResourceNotFoundException("Promocion no encontrada con codigo " + codigoNormalizado)));
    }

    public List<PromocionDTO> listarVigentes() {
        log.info("Listando promociones vigentes");
        return repository.findByActivaTrue().stream()
                .filter(this::estaVigente)
                .map(PromocionDTO::fromModel)
                .toList();
    }

    public List<PromocionDTO> buscarPorTipo(String tipo) {
        tipo = normalizarTipo(tipo);
        log.info("Buscando promociones tipo={}", tipo);
        return repository.findByTipo(tipo).stream().map(PromocionDTO::fromModel).toList();
    }

    public PromocionDTO crear(PromocionDTO dto) {
        log.info("Creando promocion codigo={}", dto != null ? dto.getCodigo() : null);
        validarDatos(dto);
        dto.setCodigo(normalizarCodigo(dto.getCodigo()));
        dto.setTipo(normalizarTipo(dto.getTipo()));
        dto.setActiva(dto.getActiva() == null ? true : dto.getActiva());

        if (repository.existsByCodigo(dto.getCodigo())) {
            throw new BadRequestException("Ya existe una promocion con ese codigo");
        }

        Promocion guardada = repository.save(dto.toModel());
        log.info("Promocion creada id={}", guardada.getId());
        return PromocionDTO.fromModel(guardada);
    }

    public PromocionDTO actualizar(Long id, PromocionDTO dto) {
        log.info("Actualizando promocion id={}", id);
        Promocion promocion = obtenerPromocion(id);
        validarDatos(dto);
        String codigo = normalizarCodigo(dto.getCodigo());
        String tipo = normalizarTipo(dto.getTipo());

        if (repository.existsByCodigoAndIdNot(codigo, id)) {
            throw new BadRequestException("Ya existe otra promocion con ese codigo");
        }

        promocion.setCodigo(codigo);
        promocion.setNombre(dto.getNombre().trim());
        promocion.setDescripcion(dto.getDescripcion().trim());
        promocion.setPorcentajeDescuento(dto.getPorcentajeDescuento());
        promocion.setFechaInicio(dto.getFechaInicio());
        promocion.setFechaFin(dto.getFechaFin());
        promocion.setActiva(dto.getActiva() == null ? promocion.getActiva() : dto.getActiva());
        promocion.setTipo(tipo);

        return PromocionDTO.fromModel(repository.save(promocion));
    }

    public PromocionDTO activar(Long id) {
        log.info("Activando promocion id={}", id);
        Promocion promocion = obtenerPromocion(id);
        promocion.setActiva(true);
        return PromocionDTO.fromModel(repository.save(promocion));
    }

    public PromocionDTO desactivar(Long id) {
        log.info("Desactivando promocion id={}", id);
        Promocion promocion = obtenerPromocion(id);
        promocion.setActiva(false);
        return PromocionDTO.fromModel(repository.save(promocion));
    }

    public ValidarPromocionResponseDTO validarPromocion(ValidarPromocionRequestDTO request) {
        if (request == null) {
            throw new BadRequestException("Los datos de validacion son obligatorios");
        }
        String codigo = normalizarCodigo(request.getCodigo());
        validarMonto(request.getMontoOriginal());

        Promocion promocion = repository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Promocion no encontrada con codigo " + codigo));

        validarPromocionAplicable(promocion);
        int descuento = calcularDescuento(request.getMontoOriginal(), promocion.getPorcentajeDescuento());

        return ValidarPromocionResponseDTO.builder()
                .valida(true)
                .mensaje("Promocion valida")
                .porcentajeDescuento(promocion.getPorcentajeDescuento())
                .montoDescuento(descuento)
                .montoFinal(request.getMontoOriginal() - descuento)
                .promocionId(promocion.getId())
                .build();
    }

    public void eliminar(Long id) {
        log.info("Eliminando promocion id={}", id);
        obtenerPromocion(id);
        repository.deleteById(id);
    }

    public int calcularDescuento(int montoOriginal, int porcentajeDescuento) {
        validarMonto(montoOriginal);
        if (porcentajeDescuento < 1 || porcentajeDescuento > 100) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 1 y 100");
        }
        return Math.round(montoOriginal * porcentajeDescuento / 100.0f);
    }

    private Promocion obtenerPromocion(Long id) {
        if (id == null) {
            throw new BadRequestException("El id de la promocion es obligatorio");
        }
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promocion no encontrada con id " + id));
    }

    private void validarDatos(PromocionDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Los datos de la promocion son obligatorios");
        }
        normalizarCodigo(dto.getCodigo());
        normalizarTipo(dto.getTipo());
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new BadRequestException("El nombre es obligatorio");
        }
        if (dto.getDescripcion() == null || dto.getDescripcion().isBlank()) {
            throw new BadRequestException("La descripcion es obligatoria");
        }
        if (dto.getPorcentajeDescuento() == null || dto.getPorcentajeDescuento() < 1 || dto.getPorcentajeDescuento() > 100) {
            throw new BadRequestException("El porcentaje de descuento debe estar entre 1 y 100");
        }
        if (dto.getFechaInicio() == null || dto.getFechaFin() == null) {
            throw new BadRequestException("Las fechas son obligatorias");
        }
        if (dto.getFechaFin().isBefore(dto.getFechaInicio())) {
            throw new BadRequestException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }
    }

    private void validarPromocionAplicable(Promocion promocion) {
        if (!Boolean.TRUE.equals(promocion.getActiva())) {
            throw new BadRequestException("La promocion no esta activa");
        }
        if (!estaVigente(promocion)) {
            throw new BadRequestException("La promocion no esta vigente");
        }
    }

    private boolean estaVigente(Promocion promocion) {
        LocalDate hoy = LocalDate.now();
        return !hoy.isBefore(promocion.getFechaInicio()) && !hoy.isAfter(promocion.getFechaFin());
    }

    private void validarMonto(Integer montoOriginal) {
        if (montoOriginal == null || montoOriginal <= 0) {
            throw new BadRequestException("El monto original debe ser mayor a cero");
        }
    }

    private String normalizarCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new BadRequestException("El codigo es obligatorio");
        }
        return codigo.trim().toUpperCase();
    }

    private String normalizarTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            throw new BadRequestException("El tipo es obligatorio");
        }
        tipo = tipo.trim().toUpperCase();
        if (!List.of("GENERAL", "USUARIO_FRECUENTE", "HORARIO_BAJA_DEMANDA", "TORNEO", "FIDELIZACION").contains(tipo)) {
            throw new BadRequestException("Tipo de promocion no valido");
        }
        return tipo;
    }
}
