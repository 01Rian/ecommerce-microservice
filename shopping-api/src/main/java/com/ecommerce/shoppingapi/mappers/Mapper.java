package com.ecommerce.shoppingapi.mappers;

public interface Mapper<E, Req, Res> {

    Res toResponse(E entity);

    E fromRequest(Req request);
}
