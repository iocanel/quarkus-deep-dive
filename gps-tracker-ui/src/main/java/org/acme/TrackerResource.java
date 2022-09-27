package org.acme;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.protobuf.Empty;

import org.acme.tracker.client.TrackerClient;
import org.jboss.resteasy.reactive.RestStreamElementType;

import io.smallrye.mutiny.Multi;

@Path("/gps")
public class TrackerResource {

  private final TrackerClient client;

  public TrackerResource(TrackerClient client) {
    this.client = client;
  }

  @GET
  @Path("/track")
  @Produces(MediaType.SERVER_SENT_EVENTS)
  @RestStreamElementType(MediaType.TEXT_PLAIN)
  public Multi<String> track() {
   return client.gps().track(Empty.getDefaultInstance()).map(p -> p.getLatitude() + " " + p.getLongitude());
  }
}
