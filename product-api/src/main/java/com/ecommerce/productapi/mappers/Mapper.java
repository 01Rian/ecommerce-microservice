package com.ecommerce.productapi.mappers;

public interface Mapper<E, Q, S> {
    S toResponse(E entity);
    E toEntity(Q request);
}
