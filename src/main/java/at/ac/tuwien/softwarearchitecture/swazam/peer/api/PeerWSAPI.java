package at.ac.tuwien.softwarearchitecture.swazam.peer.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import at.ac.tuwien.softwarearchitecture.swazam.common.infos.FingerprintSearchRequest;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.PeerFingerprintInformation;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.PeerInfo;



@Provider
@Path("/")
public class PeerWSAPI {

	//currently this control instantiates all Peer components, and manages their dependencies
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
        peerControl.getCommunicationManager().forwardSearchRequest(searchRequest.getClientInfo(), searchRequest.getFingerprint());
    }

    /**
     * Used in heart beat from SuperPeer to normal peers.
     */
    @PUT
    @Path("/superPeerInfo")
    @Consumes("multipart/form")
    public void refreshSuperPeerInfo(PeerInfo superPeerInfo) {
        Logger.getLogger(PeerWSAPI.class).log(Level.WARN, "Updated super peer ID: " + superPeerInfo.getPeerID());
        peerControl.getPeerManager().updateSuperPeerInfo(superPeerInfo);
    }
    
    /**If SuperPeer info not updated at specific interval, super peer is considered dead and leader election takes place
    * @param superPeerInfo contains at least the UUID of the super peer */
    @POST
    @Path("/superPeerInfo")
    @Consumes("multipart/form")
    public void leaderEllectionSuperPeerInfo(PeerInfo superPeerInfo) {
        Logger.getLogger(PeerWSAPI.class).log(Level.WARN, "Updated super peer ID: " + superPeerInfo.getPeerID());
        peerControl.getPeerManager().performLeaderElection();
    }
    
    @PUT
    @Path("/fingerprints")
    @Consumes("multipart/form")
    public void refreshSuperPeerInfo(PeerFingerprintInformation peerFingerprintInformation) {
    
        Logger.getLogger(PeerWSAPI.class).log(Level.WARN, "Updated super peer ID: " + peerFingerprintInformation.getPeerInfo().getIp());
        peerControl.getPeerManager().updateRingInformation(peerFingerprintInformation);
    }
    
}