package com.jonfreer.wedding.hk2;

import com.jonfreer.wedding.api.converters.GuestCollectionConverter;
import com.jonfreer.wedding.api.converters.GuestConverter;

public class ConverterBinder extends org.glassfish.hk2.utilities.binding.AbstractBinder{

	@Override
	protected void configure() {
		this.bind(GuestConverter.class).to(GuestConverter.class);
		this.bind(GuestCollectionConverter.class).to(GuestCollectionConverter.class);
	}
}
