package com.ecommerce.userapi.mapper;

public interface Mapper<A, B> {

    B mapTo(A a);

    A mapFrom(B a);
}
