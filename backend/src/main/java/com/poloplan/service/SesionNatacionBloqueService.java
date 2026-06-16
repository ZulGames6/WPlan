package com.poloplan.service;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.poloplan.dto.SesionNatacionDtos.ActualizarBloqueRequest;
import com.poloplan.dto.SesionNatacionDtos.ActualizarItemRequest;
import com.poloplan.dto.SesionNatacionDtos.AnadirItemRequest;
import com.poloplan.dto.SesionNatacionDtos.BloqueResponse;
import com.poloplan.dto.SesionNatacionDtos.CrearBloqueRequest;
import com.poloplan.dto.SesionNatacionDtos.ItemResponse;
import com.poloplan.entity.AppUser;
import com.poloplan.entity.EjercicioNatacion;
import com.poloplan.entity.SesionDia;
import com.poloplan.entity.SesionNatacion;
import com.poloplan.entity.SesionNatacionBloque;
import com.poloplan.entity.SesionNatacionBloqueItem;
import com.poloplan.repository.EjercicioNatacionRepository;
import com.poloplan.repository.SesionDiaRepository;
import com.poloplan.repository.SesionNatacionBloqueItemRepository;
import com.poloplan.repository.SesionNatacionBloqueRepository;

@Service
public class SesionNatacionBloqueService {

  private final SesionDiaRepository sesionDiaRepository;
  private final EjercicioNatacionRepository ejercicioRepository;
  private final SesionNatacionBloqueRepository bloqueRepository;
  private final SesionNatacionBloqueItemRepository itemRepository;

  public SesionNatacionBloqueService(
    SesionDiaRepository sesionDiaRepository,
    EjercicioNatacionRepository ejercicioRepository,
    SesionNatacionBloqueRepository bloqueRepository,
    SesionNatacionBloqueItemRepository itemRepository
  ) {
    this.sesionDiaRepository = sesionDiaRepository;
    this.ejercicioRepository = ejercicioRepository;
    this.bloqueRepository = bloqueRepository;
    this.itemRepository = itemRepository;
  }

  @Transactional
  public BloqueResponse crearBloque(AppUser propietario, Long planNumero, Long sesionId, CrearBloqueRequest request) {
    SesionDia s = requireSesion(propietario, planNumero, sesionId);
    SesionNatacion sn = ensureNatacionPart(s);

    int orden = request.orden() != null
      ? request.orden()
      : bloqueRepository.maxOrden(Objects.requireNonNull(sn.getId())) + 1;

    SesionNatacionBloque b = new SesionNatacionBloque();
    b.setSesionNatacion(sn);
    b.setOrden(orden);
    b.setNombre(blancoANulo(request.nombre()));
    b.setTipoBloque(upper(request.tipoBloque()));
    b.setDescripcion(blancoANulo(request.descripcion()));
    b.setSeries(request.series());
    b.setMetrosPorSerie(request.metrosPorSerie());
    b.setDescansoSeg(request.descansoSeg());
    b.setIntensidadAE(upper(request.intensidadAE()));
    b.setMaterial(blancoANulo(request.material()));
    b.setNotas(blancoANulo(request.notas()));

    bloqueRepository.save(b);
    return aRespuestaBloque(b);
  }

  public List<BloqueResponse> listarBloques(AppUser propietario, Long planNumero, Long sesionId) {
    requireSesion(propietario, planNumero, sesionId);
    return bloqueRepository.listBySesionAndOwner(planNumero, sesionId, propietario.getId())
      .stream().map(this::aRespuestaBloque).toList();
  }

  @Transactional
  public BloqueResponse actualizarBloque(AppUser propietario, Long planNumero, Long sesionId, Long bloqueId, ActualizarBloqueRequest request) {
    SesionNatacionBloque b = requireBloque(propietario, planNumero, sesionId, bloqueId);
    if (request.orden() != null) b.setOrden(request.orden());
    b.setNombre(blancoANulo(request.nombre()));
    b.setTipoBloque(upper(request.tipoBloque()));
    b.setDescripcion(blancoANulo(request.descripcion()));
    if (request.series() != null) b.setSeries(request.series());
    if (request.metrosPorSerie() != null) b.setMetrosPorSerie(request.metrosPorSerie());
    if (request.descansoSeg() != null) b.setDescansoSeg(request.descansoSeg());
    b.setIntensidadAE(upper(request.intensidadAE()));
    b.setMaterial(blancoANulo(request.material()));
    b.setNotas(blancoANulo(request.notas()));
    bloqueRepository.save(b);
    return aRespuestaBloque(b);
  }

  @Transactional
  public void eliminarBloque(AppUser propietario, Long planNumero, Long sesionId, Long bloqueId) {
    SesionNatacionBloque b = requireBloque(propietario, planNumero, sesionId, bloqueId);
    bloqueRepository.deleteById(Objects.requireNonNull(b.getId()));
  }

  @Transactional
  public ItemResponse anadirItem(AppUser propietario, Long planNumero, Long sesionId, Long bloqueId, AnadirItemRequest request) {
    SesionNatacionBloque b = requireBloque(propietario, planNumero, sesionId, bloqueId);
    EjercicioNatacion ej = ejercicioRepository.findByIdAndOwner(request.ejercicioId(), propietario.getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ejercicio de natación no encontrado"));

    int orden = request.orden() != null
      ? request.orden()
      : itemRepository.maxOrden(Objects.requireNonNull(b.getId())) + 1;

    SesionNatacionBloqueItem i = new SesionNatacionBloqueItem();
    i.setBloque(b);
    i.setEjercicio(ej);
    i.setOrden(orden);
    i.setSeries(request.series());
    i.setMetrosPorSerie(request.metrosPorSerie());
    i.setDescansoSeg(request.descansoSeg());
    i.setMaterial(blancoANulo(request.material()));
    i.setNotas(blancoANulo(request.notas()));

    itemRepository.save(i);
    return aRespuestaItem(i);
  }

  @Transactional
  public ItemResponse actualizarItem(AppUser propietario, Long planNumero, Long sesionId, Long bloqueId, Long itemId, ActualizarItemRequest request) {
    SesionNatacionBloqueItem i = itemRepository.findOwned(planNumero, sesionId, bloqueId, itemId, propietario.getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ítem no encontrado"));
    if (request.orden() != null) i.setOrden(request.orden());
    if (request.series() != null) i.setSeries(request.series());
    if (request.metrosPorSerie() != null) i.setMetrosPorSerie(request.metrosPorSerie());
    if (request.descansoSeg() != null) i.setDescansoSeg(request.descansoSeg());
    i.setMaterial(blancoANulo(request.material()));
    i.setNotas(blancoANulo(request.notas()));
    itemRepository.save(i);
    return aRespuestaItem(i);
  }

  @Transactional
  public void eliminarItem(AppUser propietario, Long planNumero, Long sesionId, Long bloqueId, Long itemId) {
    SesionNatacionBloqueItem i = itemRepository.findOwned(planNumero, sesionId, bloqueId, itemId, propietario.getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ítem no encontrado"));
    itemRepository.deleteById(Objects.requireNonNull(i.getId()));
  }

  private SesionDia requireSesion(AppUser propietario, Long planNumero, Long sesionId) {
    return sesionDiaRepository.findByIdAndPlanNumeroAndOwner(sesionId, planNumero, propietario.getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada"));
  }

  private SesionNatacionBloque requireBloque(AppUser propietario, Long planNumero, Long sesionId, Long bloqueId) {
    return bloqueRepository.findOwned(planNumero, sesionId, bloqueId, propietario.getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bloque no encontrado"));
  }

  private SesionNatacion ensureNatacionPart(SesionDia s) {
    if (s.getNatacion() != null) return s.getNatacion();
    SesionNatacion sn = new SesionNatacion();
    sn.setSesionDia(s);
    s.setNatacion(sn);
    sesionDiaRepository.save(s);
    return sn;
  }

  private static String upper(String s) {
    String t = blancoANulo(s);
    return t == null ? null : t.toUpperCase();
  }

  private static String blancoANulo(String s) {
    if (s == null) return null;
    String t = s.trim();
    return t.isEmpty() ? null : t;
  }

  private BloqueResponse aRespuestaBloque(SesionNatacionBloque b) {
    List<ItemResponse> items = b.getItems().stream().map(this::aRespuestaItem).toList();
    int selfMetros = b.metrosTotales();
    int itemsMetros = items.stream().mapToInt(i -> i.metrosTotales() == null ? 0 : i.metrosTotales()).sum();
    int totalMetros = selfMetros > 0 ? selfMetros : itemsMetros;
    double cargaEstimada = b.cargaEstimada();
    return new BloqueResponse(
      b.getId(), b.getOrden(), b.getNombre(), b.getTipoBloque(),
      b.getDescripcion(), b.getSeries(), b.getMetrosPorSerie(), b.getDescansoSeg(),
      b.getIntensidadAE(), b.getMaterial(), b.getNotas(),
      items, totalMetros, Math.round(cargaEstimada * 10.0) / 10.0
    );
  }

  private ItemResponse aRespuestaItem(SesionNatacionBloqueItem i) {
    EjercicioNatacion ej = i.getEjercicio();
    return new ItemResponse(
      i.getId(), ej.getId(), ej.getNombre(), ej.getEstilo(),
      i.getOrden(), i.getSeries(), i.getMetrosPorSerie(), i.getDescansoSeg(),
      i.getMaterial(), i.getNotas(), i.metrosTotales()
    );
  }
}
