package at.ac.tuwien.softwarearchitecture.swazam.peer.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import at.ac.tuwien.softwarearchitecture.swazam.common.infos.FingerprintSearchRequest;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.PeerFingerprintInformation;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.PeerInfo;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.PeerRingInformation;
import at.ac.tuwien.softwarearchitecture.swazam.peer.serverCommunication.IServerCommunicationManager;

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
    @Consumes(MediaType.APPLICATION_XML)
    public void searchFingerprint(FingerprintSearchRequest searchRequest) {
        Logger.getLogger(PeerWSAPI.class).log(Level.WARN,"SessionID for search " + searchRequest.getClientInfo().getSessionKey());
        peerControl.getCommunicationManager().matchFile(searchRequest.getClientInfo(), searchRequest.getFingerprint());
    }
 
    /**
     * Used in heart beat from SuperPeer to normal peers.
     */
    @PUT
    @Path("/updateRingInformation")
    @Consumes(MediaType.APPLICATION_XML)
    public void updateRingInformation(PeerRingInformation peerRingInformation) {
       
        peerControl.getPeerManager().updateRingInformation(peerRingInformation);
    }
    
    /**If SuperPeer info not updated at specific interval, super peer is considered dead and leader election takes place
    * @param superPeerInfo contains at least the UUID of the super peer */
    @POST
    @Path("/leaderEllectionSuperPeerInfo")
    @Consumes(MediaType.APPLICATION_XML)
    public void leaderEllectionSuperPeerInfo(PeerInfo superPeerInfo) {
        Logger.getLogger(PeerWSAPI.class).log(Level.WARN, "Updated super peer ID: " + superPeerInfo.getPeerID());
        peerControl.getPeerManager().performLeaderElection();
    }
    
    @PUT
    @Path("/fingerprints")
    @Consumes(MediaType.APPLICATION_XML)
    public void addFingerprints(PeerFingerprintInformation peerFingerprintInformation) {
        Logger.getLogger(PeerWSAPI.class).log(Level.WARN, "Adding fingeprints from peer: " + peerFingerprintInformation.getPeerInfo().getPort() + " size " + peerFingerprintInformation.getFingerprints().size());
        peerControl.getPeerManager().distributeFingerprintsToSuperpeer(peerFingerprintInformation);
    }
    
    @GET
    @Path("/initiate")
    public String test(){
    	return "Peer Initiated";
    }
    
}