package co.Proyecto_Backendll.service;

import co.Proyecto_Backendll.api.Dtos.PageResponse;
import co.Proyecto_Backendll.common.ApiException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Set;
import java.util.function.Function;

/** Validacion de paginacion/orden y construccion de PageResponse. */
final class PageSupport {

    private PageSupport() {}

    static PageRequest pageRequest(int page, int size, String sort, String dir, Set<String> allowedSorts) {
        if (page < 0) {
            throw ApiException.badRequest("El número de página no puede ser negativo");
        }
        if (size < 1 || size > 100) {
            throw ApiException.badRequest("El tamaño de página debe estar entre 1 y 100");
        }
        if (!allowedSorts.contains(sort)) {
            throw ApiException.badRequest("Ordenamiento no permitido: " + sort);
        }
        Sort.Direction direction;
        if ("asc".equalsIgnoreCase(dir)) {
            direction = Sort.Direction.ASC;
        } else if ("desc".equalsIgnoreCase(dir)) {
            direction = Sort.Direction.DESC;
        } else {
            throw ApiException.badRequest("La dirección debe ser asc o desc");
        }
        return PageRequest.of(page, size, Sort.by(new Sort.Order(direction, sort), Sort.Order.asc("id")));
    }

    /** Patron LIKE: vacio o nulo = todo. */
    static String pattern(String q) {
        if (q == null || q.trim().isEmpty()) {
            return "%";
        }
        return "%" + q.trim() + "%";
    }

    static <E, T> PageResponse<T> toResponse(Page<E> page, Function<E, T> mapper) {
        return new PageResponse<>(
                page.getContent().stream().map(mapper).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
