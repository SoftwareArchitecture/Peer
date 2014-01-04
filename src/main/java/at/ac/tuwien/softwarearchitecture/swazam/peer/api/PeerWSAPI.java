package at.ac.tuwien.softwarearchitecture.swazam.peer.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import at.ac.tuwien.softwarearchitecture.swazam.common.infos.FingerprintSearchRequest;
import at.ac.tuwien.softwarearchitecture.swazam.peer.util.PeerControl;

@Provider
@Path("/")
public class PeerWSAPI {

    private PeerControl peerControl;

    {
        peerControl = PeerControl.getInstance();
    }

    @POST
    @Path("/search")
    @Consumes("multipart/form")
    public void searchFingerprint(FingerprintSearchRequest searchRequest) {
        Logger.getLogger(PeerWSAPI.class).log(Level.WARN, searchRequest.getClientInfo().getClientID());
        Logger.getLogger(PeerWSAPI.class).log(Level.WARN, searchRequest.getFingerprint().getShiftDuration());
    }

    @GET
    @Path("/test")
    public String test() {
        return "SSSS";
    }
}