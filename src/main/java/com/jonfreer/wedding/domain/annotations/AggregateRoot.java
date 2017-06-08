package com.jonfreer.wedding.domain.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author jonfreer
 *
 */
@Documented
@Retention(SOURCE)
@Target(TYPE)
public @interface AggregateRoot {}
