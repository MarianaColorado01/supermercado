/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.api.superm.supermercado.Service;

import com.api.superm.supermercado.Model.atencion;
import com.api.superm.supermercado.Model.categoriaPrioridad;
import com.api.superm.supermercado.Model.empleado;
import com.api.superm.supermercado.Model.estadoTurno;
import com.api.superm.supermercado.Model.rolEmpleado;
import com.api.superm.supermercado.Model.turno;
import com.api.superm.supermercado.Repository.atencionRepository;
import com.api.superm.supermercado.Repository.categoriaPrioridadRepository;
import com.api.superm.supermercado.Repository.empleadoRepository;
import com.api.superm.supermercado.Repository.turnoRepository;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

/**
 *
 * @author mariana01colorado
 */


@Service
@RequiredArgsConstructor
public class turnoService {
    
    private final turnoRepository turnoRepository;
    private final categoriaPrioridadRepository categoriaRepository;
    private final atencionRepository atencionRepository;
    
    
    private int contadorPrioritarios = 0;
    private int numeroCorrelativo = 1;  // contsador de personas regulares
    
    public turno crearTurno(Long categoriaId){
        categoriaPrioridad categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));
        
        turno turno = new turno();
        turno.setNumero(numeroCorrelativo++);
        turno.setCategoria(categoria);
        turno.setEstado(estadoTurno.ESPERA);
        turno.setFechaCreacion(LocalDateTime.now());

        return turnoRepository.save(turno);
    }
    
    public List<turno> listar() {
        return turnoRepository.findAll();
    }
    
    
    @Transactional
    public Optional<turno> llamarSiguiente(){
        List<turno> enEspera = turnoRepository.findByEstado(estadoTurno.ESPERA);
        
        if(enEspera.isEmpty()) return Optional.empty();
        
        enEspera.forEach(t -> {
            long minutos = Duration.between(t.getFechaCreacion(), LocalDateTime.now()).toMinutes();
            if(t.getCategoria().getNombre().equalsIgnoreCase("Cliente normal")&& minutos>7){
                t.setAging(true);
            }
        
        });
        
        Optional<turno> siguiente;
        if(contadorPrioritarios < 3){
            siguiente = enEspera.stream()
                    .filter(t -> !t.getCategoria().getNombre().equalsIgnoreCase("cliente normal"))
                    .min(Comparator.comparing(turno::getFechaCreacion));
            if( siguiente.isPresent()) contadorPrioritarios++;
            else contadorPrioritarios = 0;
        }else{
            siguiente = enEspera.stream()
                    .filter(t -> t.getCategoria().getNombre().equalsIgnoreCase("Cliente normal"))
                    .min(Comparator.comparing(turno::getFechaCreacion));
            contadorPrioritarios = 0;
        }
        
        siguiente.ifPresent(t -> {
            t.setEstado(estadoTurno.ATENDIDO);
            t.setFechaLlamado(LocalDateTime.now());
            turnoRepository.save(t);
        });
        
        return siguiente;
    }
    
    @Transactional
    public turno finalizar(Long id, empleado empleado){
        turno t = turnoRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turno no encontrado"));
        
        t.setEstado(estadoTurno.FINALIZADO);
        t.setFechaFinalizado(LocalDateTime.now());
        t.setTiempoEspera(Duration.between(t.getFechaCreacion(), t.getFechaLlamado()).toSeconds());
        t.setEmpleado(empleado);
        turnoRepository.save(t);
        
        atencion ate = new atencion();
        ate.setTurno(t);
        ate.setEmpleado(empleado);
        ate.setFechaInicio(t.getFechaLlamado());
        ate.setFechaFin(t.getFechaFinalizado());
        ate.setDuracionSegundos(
                Duration.between(t.getFechaLlamado(), t.getFechaFinalizado()).toSeconds()
        );

        atencionRepository.save(ate);

        return t;
    }
   
    public void eliminar(Long idTurno, empleado empleadoAutenticado) {

        turno t = turnoRepository.findById(idTurno)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turno no encontrado"));

        if (empleadoAutenticado.getRol() != rolEmpleado.SUPERVISOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo un supervisor puede eliminar turnos");
        }

        turnoRepository.delete(t);
    }
}
