package com.jonfreer.wedding.api.converters;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.ws.rs.core.UriBuilder;

import com.jonfreer.wedding.servicemodel.Guest;

import siren.Action;
import siren.EmbeddedLinkSubEntity;
import siren.Entity;
import siren.HttpMethod;
import siren.Link;
import siren.Relation;

public class GuestCollectionConverter {

	public Entity convert(List<Guest> guests, URI requestUri, Integer skip, Integer take, Integer total) throws URISyntaxException {
		
		Link.Builder linkBuilder = new Link.Builder();
		Entity.Builder entityBuilder = new Entity.Builder();
		Action.Builder actionBuilder = new Action.Builder();
		EmbeddedLinkSubEntity.Builder linkSubEntityBuilder = new EmbeddedLinkSubEntity.Builder();
		final String sirenMediaType = "application/vnd.siren+json";
		final String jsonMediaType = "application/json";

		boolean hasPreviousLink = this.hasPreviousLink(skip, take, total);
		boolean hasNextLink = this.hasNextLink(skip, take, total);

		if(hasPreviousLink) {
			Integer prevSkip = skip - take >= 0 ? skip - take : 0;
			Integer prevTake = skip - prevSkip < take ? skip - prevSkip : take;

			URI prevHref = 
				UriBuilder
					.fromUri(requestUri)
					.replaceQueryParam("skip", prevSkip)
					.replaceQueryParam("take", prevTake)
					.build();

			Link prevLink = 
					linkBuilder
					.rel(Relation.PREV)
					.title("previous")
					.type(sirenMediaType)
					.href(prevHref)
					.build();

			entityBuilder.link(prevLink);
			linkBuilder.clear();
		}

		if(hasNextLink) {
			int nextSkip = skip + take;
			int nextTake = take;

			URI nextHref = 
				UriBuilder
					.fromUri(requestUri)
					.replaceQueryParam("skip", nextSkip)
					.replaceQueryParam("take", nextTake)
					.build();					

			Link nextLink = 
					linkBuilder
					.rel(Relation.NEXT)
					.title("next")
					.type(sirenMediaType)
					.href(nextHref)
					.build();

			entityBuilder.link(nextLink);
			linkBuilder.clear();
		}

		Link selfLink = 
				linkBuilder
				.rel(Relation.SELF)
				.title("self")
				.type(sirenMediaType)
				.href(requestUri)
				.build();

		entityBuilder.link(selfLink);
		linkBuilder.clear();

		Action addGuest = 
			actionBuilder
				.method(HttpMethod.POST)
				.href(UriBuilder.fromUri(requestUri).replaceQuery("").build())
				.title("Add Guest")
				.name("add-guest")
				.type(jsonMediaType)
				.build();

		actionBuilder.clear();
		entityBuilder.actions(addGuest);

		for(Guest guest : guests) {
			URI href = 
				UriBuilder
					.fromUri(requestUri)
					.replaceQuery("")
					.path("/{id}/")
					.build(guest.getId());

			EmbeddedLinkSubEntity linkSubEntity = 
				linkSubEntityBuilder
					.rel(Relation.ITEM)
					.href(href)
					.klass("Wedding Guest")
					.title("Wedding Guest")
					.type(sirenMediaType)
					.build();
			
			entityBuilder.subEntity(linkSubEntity);
			linkSubEntityBuilder.clear();
		}
		
		entityBuilder.klasses("guest", "collection");
		entityBuilder.title("Wedding Guests");

		return entityBuilder.build();
	}

	private boolean hasPreviousLink(Integer skip, Integer take, Integer total) {
		boolean hasPrevious = false;
		if (take != null && skip != null) {		
			hasPrevious = skip > 0;
		}
		return hasPrevious;
	}

	private boolean hasNextLink(Integer skip, Integer take, Integer total) {
		boolean hasNext = false;
		if(take != null) {
			hasNext = total - take > 0;
			if(skip != null) {
				hasNext = total - (skip + take) > 0;
			}
		}	
		return hasNext;
	}
}
