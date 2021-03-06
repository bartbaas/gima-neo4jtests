/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gima.neo4j.testsuite.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import gima.neo4j.testsuite.client.GwtService;
import gima.neo4j.testsuite.shared.Messages;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;

/**
 *
 * @author bartbaas
 */
public class GwtServiceImpl extends RemoteServiceServlet implements GwtService {

    private static File basePath = new File(System.getProperty("user.home") + "/data/neodb");
    private Neo simpleDb = new Neo();
    private Neo medemblikDb = new Neo();
    private Neo amsterdamDb = new Neo();
    private Neo nhDb = new Neo();

    public String SendTask(Messages.Type type, Messages.Db db, double[][] obj, boolean store) {

        switch (db) {
            case MEDEMBLIK:
                if (!medemblikDb.isRunning()) {
                    medemblikDb.layerName = "Medemblik";
                    medemblikDb.dbPath = new File(basePath, "medemblik.gdb");
                    medemblikDb.osmfile = (System.getProperty("user.home") + "/data/osm/medemblik.osm");
                    medemblikDb.mapConfig = Neo.NORMAL_CONFIG;
                }
                /**if (!simpleDb.isRunning()) {
                    simpleDb.layerName = "Simple";
                    simpleDb.dbPath = new File(basePath, "simple.gdb");
                    simpleDb.osmfile = (System.getProperty("user.home") + "/data/osm/simple.osm");
                    simpleDb.mapConfig = Neo.NORMAL_CONFIG;
                    try {
                    simpleDb.ImportOSM_Batch(true);
                    } catch (Exception ex) { }
                }
                */
                return SendOperation(medemblikDb, type, obj, store);
            case AMSTERDAM:
                if (!amsterdamDb.isRunning()) {
                    amsterdamDb.layerName = "Amsterdam";
                    amsterdamDb.dbPath = new File(basePath, "amsterdam.gdb");
                    amsterdamDb.osmfile = (System.getProperty("user.home") + "/data/osm/amsterdam.osm");
                    amsterdamDb.mapConfig = Neo.NORMAL_CONFIG;
                }
                return SendOperation(amsterdamDb, type, obj, store);
            case NH:
                if (!nhDb.isRunning()) {
                    nhDb.layerName = "North-Holland";
                    nhDb.dbPath = new File(basePath, "north-holland.gdb");
                    nhDb.osmfile = (System.getProperty("user.home") + "/data/osm/north-holland.osm");
                    nhDb.mapConfig = Neo.LARGE_CONFIG;
                }
                return SendOperation(nhDb, type, obj, store);
            default:
                return "<div class=red>Database not found.</red>";
        }
        //Logger.getLogger(MedemblikServiceImpl.class.getName()).log(Level.INFO, null, "Executing task :" + type);
    }

    private String SendOperation(Neo instance, Messages.Type type, double[][] obj, boolean store) {
        try {
            switch (type) {
                case TEST_EMPTY:
                    return "Nothing to do";
                case START:
                    return instance.Start();
                case STOP:
                    return instance.Stop();
                case STATISTICS:
                    return instance.Stats();
                case STATUS:
                    return null;
                case DOTESTS:
                    return instance.DoTests();
                case MAKE_OSM:
                    //if (store) {
                    //    return instance.ImportOSM(store);
                    //} else {
                        return instance.ImportOSM_Batch(store);
                    //}
                case MAKE_NETWORK:
                    return instance.MakeTopology();
                case MAKE_DYNAMICLAYERS:
                    return instance.AddDynamicLayers();
                case DELETE:
                    return instance.Delete();
                case TEST_CLOSEPOINT:
                    return instance.findClosestNode(obj, store);
                case TEST_BOUNDINGBOX:
                    return instance.searchBbox(obj, store);
                case TEST_GML:
                    return instance.getGML(obj, store);
                case TEST_ROUTE:
                    return instance.ShortestPath(obj, store);
                case TEST_JOIN:
                    return instance.SpatialJoin(obj, store);
                default:
                    return "<div class=red>Not implemented yet.</red>";
            }
        } catch (Exception ex) {
            return (ex.toString());
        }
    }
}
