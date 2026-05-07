package com.juratempest.ms_reservas.model;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reservas",
            uniqueConstraints = {
                @UniqueConstraint(columnNames = {"maquina", "bloque_horario"})
            }
    )


public class Reservas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId;
    private Long maquinaId;
    private Long bloqueId;
    private LocalDateTime fechaReserva;
    private String estado;
    
}
