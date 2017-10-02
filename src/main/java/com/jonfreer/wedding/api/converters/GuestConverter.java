package com.jonfreer.wedding.api.converters;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.MediaType;

import com.jonfreer.wedding.servicemodel.Guest;

import siren.Action;
import siren.Entity;
import siren.Field;
import siren.FieldType;
import siren.HttpMethod;
import siren.Link;
import siren.Relation;
import siren.factories.ActionBuilderFactory;
import siren.factories.EntityBuilderFactory;
import siren.factories.FieldBuilderFactory;
import siren.factories.LinkBuilderFactory;

public class GuestConverter{

	public GuestConverter() {}

	public Object convert(Guest guest, URI requestUri) {
				
		Entity.Builder entityBuilder = new EntityBuilderFactory().create();
		Link.Builder linkBuilder = new LinkBuilderFactory().create();
		Action.Builder actionBuilder = new ActionBuilderFactory().create();
		Field.Builder<String> stringFieldBuilder = new FieldBuilderFactory<String>().create();

		try {
			Link selfLink = 
				linkBuilder
				.href(requestUri)
				.rel(Relation.SELF)
				.type("application/json")
				.title("Self")
				.build();

			Field<String> givenNameField =
				stringFieldBuilder
				.name("givenName")
				.title("Given Name")
				.type(FieldType.TEXT)
				.build();

			stringFieldBuilder.clear();

			Field<String> surnameField =
				stringFieldBuilder
				.name("surName")
				.title("Surname")
				.type(FieldType.TEXT)
				.build();

			stringFieldBuilder.clear();

			Field<String> descriptionField =
				stringFieldBuilder
				.name("description")
				.title("Description")
				.type(FieldType.TEXT)
				.build();

			stringFieldBuilder.clear();

			Field<String> inviteCodeField =
				stringFieldBuilder
				.name("inviteCode")
				.title("Invite Code")
				.type(FieldType.TEXT)
				.build();

			Action deleteGuestAction = 
				actionBuilder
				.method(HttpMethod.DELETE)
				.title("Delete Guest")
				.name("delete-guest")
				.href(requestUri)
				.type(MediaType.APPLICATION_JSON)
				.build();

			actionBuilder.clear();

			Action replaceGuestAction = 
				actionBuilder
				.method(HttpMethod.PUT)
				.title("Replace Guest")
				.name("replace-guest")
				.href(requestUri)
				.fields(givenNameField, surnameField, descriptionField, inviteCodeField)
				.type(MediaType.APPLICATION_JSON)
				.build();

			entityBuilder
				.klass("guest")
				.title("Wedding Guest")
				.property("id", guest.getId())
				.property("givenName", guest.getGivenName())
				.property("surName", guest.getSurName())
				.property("inviteCode", guest.getInviteCode())
				.property("description", guest.getDescription())
				.property("dietaryRestrictions", guest.getDietaryRestrictions())
				.link(selfLink)
				.actions(replaceGuestAction, deleteGuestAction);

			if(guest.getReservation() != null) {
				entityBuilder.property("reservation", guest.getReservation());
			}

			Entity sirenGuest = entityBuilder.build();
			return sirenGuest;

		} catch (URISyntaxException e) {
			e.printStackTrace();
			return new RuntimeException(e);
		}
	}
}
