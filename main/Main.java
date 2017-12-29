package main;

import gui.Gui;
import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import node.Node;
import node.NodeConnection;

class Main {
    private int numCon = 3; 
    private int numNodes = 30;
    private int tries = 1000;
    
    private volatile List<Node> recoveringnodes = new ArrayList<>();
    private volatile List<Node> infectednodes = new ArrayList<>();
    private List<Node> nodes = new ArrayList<>();
    private List<NodeConnection> connections = new ArrayList<>();
    private Gui gui;
    
    public Main(){
        try {
            gui = new Gui();
            //gui.start();
            
            //tries = nodes.size()*(numNodes);
            //setupNodes(numNodes);
            //makeConnections(numCon);
        } catch (InterruptedException | InvocationTargetException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void setupNodes(int numNodes){
        for(int x = 0; x < numNodes; x++){
            Node node = new Node(x);
            
            int rx = new Random().nextInt(gui.SSIZE.width-20)+10;
            int ry = new Random().nextInt(gui.SSIZE.height-20)+10;
            
            node.getNodeLink().setX(rx);
            node.getNodeLink().setY(ry);
            nodes.add(node);
            gui.addNode(node);
            gui.addBot(node.getNodeLink().getX(), node.getNodeLink().getY(),10,10);
        }
        gui.setNodes(nodes);
    }
    public void makeConnections(int num){
        //num *= 2;
        if(num > nodes.size()-1){
           return;
        }
        
        for(int y = 1; y < num+1; y++){
            int yy = y - 1;
            for(int x = 0; x < nodes.size(); x++){
                boolean gotNode = false;
                Node n = null;
                
                do{
                    boolean all = true;
                    
                    for(Node node: nodes){
                        
                        if(node.getConnected().size() == yy){
                            all = false;
                        }
                    }
                    //all has num connections
                    if(all){
                        break;
                    }

                    int r = new Random().nextInt(nodes.size());
                    
                    
                    if(r != x && nodes.get(r).getConnected().size() == yy &&
                            !nodes.get(x).getConnected().contains(nodes.get(r))){
                        
                        n = nodes.get(r);
                        gotNode = true;
                    }
                    
                }while(!gotNode);

                if(nodes.get(x).getConnected().size() == yy){
                    nodes.get(x).addConnected(n);
                }
            }
        }
        
        //connections.clear();
        connections.addAll(getConnections());
        gui.setConnections(connections);
    }
    private List<NodeConnection> getConnections(){
        List<NodeConnection> cn = new ArrayList<>();
        for(Node node: nodes){
            //connections
            for(Object object: node.getConnections()){
                NodeConnection nc = (NodeConnection) object;
                if(!cn.contains(nc)){
                    cn.add(nc);
                }
            }
        }
        return cn;
    }
    
    private void mutateNode(Node node, int tries) throws InterruptedException{
        //Color randColor = new Color(new Random().nextInt(255),new Random().nextInt(255),new Random().nextInt(255));
        
        
        int numOfInfected = 0;
        node.setOk(false);
        Node in = null;
        
        Thread.sleep(1000);
        
        boolean success = false;
        for(int x = 0; x < tries; x++){
            //System.out.println("Try - " + x);
            if(infectednodes.size() == nodes.size()){
                System.out.println("ALL NODES INFECTED");
                success = true;
                break;
            }
            if(in == null){
                in = node;
            }
            if(!infectednodes.contains(in)){
                infectednodes.add(in);
            }
            
            //System.out.println("INFECTED: " + infectednodes);
            //System.out.println("RECOVERING: " + recoveringnodes);
            //System.out.println("RECOVERED: " + recoveringnodes);
            
            boolean pass = false;
            int rc = 0;
            int sleep = 1;
            do{
                //get random connection from in
                rc = new Random().nextInt(in.getConnected().size());
                Node rcn = (Node) in.getConnected().get(rc);
                
                
                
                //link not ok
                if(!rcn.getOk()){
                    //x++;
                    continue;
                }else if(!rcn.allConnectionsOk()){
                    System.out.println("ALL CONNECTIONS INFECTED TO NODE:" + rcn);
                    int is = new Random().nextInt(infectednodes.size());
                    in = (Node) infectednodes.get(is); 
                    //x++;
                    continue;
                }else if(rcn.getOk() || rcn.recovering){
                    if(rcn.recovering){
                        rcn.setRecovering(false);
                        recoveringnodes.remove(rcn);
                    }
                    if(in.getNodeLink().infectLink(rcn)){
                        infectednodes.add(rcn);
                    }else{
                        recoverNode(rcn);
                    }
                    sleep = rcn.diff;
                    pass = true;
                }
            }while(!pass);
            
            try {
                Thread.sleep((50*sleep)+sleep);
            } catch (Exception ex) {}
            
            
            
            if(!in.recovering){
                recoverNode(in);
            }
            int is = infectednodes.size();
            in = (Node) infectednodes.get(is-1); 
            
        }
        System.out.println("END OF TRIES");
        //break
    }
    private synchronized void recoverNode(Node node){
        if(!node.getOk()){
            
            new Thread(new Runnable(){
                @Override
                public void run(){
                    try {
                        Thread.sleep( ( ( (node.HIGHDIF + node.LOWDIF)-node.diff ) /2 ) *1000);
                    } catch (InterruptedException ex) {}
                    
                    
                    node.recovering = true;
                    node.setRecovering(true);
                    
                    while(node.recovering){
                        //recovers itself and either fights node or severs connection
                        boolean win = false;
                        
                        for(int x = 0; x < node.HIGHDIF-node.diff; x++){
                            try {
                                Thread.sleep(50*node.diff);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            
                            if(!recoveringnodes.contains(node)){
                                recoveringnodes.add(node);
                            }
                            
                            
                            
                            int ra = new Random().nextInt();
                            int rb = new Random().nextInt()/2;
                            
                            if(ra > rb){
                                win = false;
                            }
                            if(rb > ra){
                                if(new Random().nextBoolean()){
                                    win = true;
                                }
                            }
                        }
                        if(win){
                            node.recovering = false;
                            node.setRecovering(false);
                        }
                    }
                    node.setOk(true);
                    node.setRecovered(true);
                    
                    Iterator<Node> ii = infectednodes.iterator();
                    while(ii.hasNext()){
                        Node n = ii.next();
                        
                        if(n.equals(node)){
                            ii.remove();
                        }
                    }
                    Iterator<Node> ri = recoveringnodes.iterator();
                    while(ri.hasNext()){
                        Node n = ri.next();
                        if(n.equals(node)){
                            ri.remove();
                        }
                    }
                }
            }).start();
        }
    }
    
    public static void main(String[] args){
        Main main = new Main();
        main.setupNodes(main.numNodes);
        main.makeConnections(main.numCon);
        
        Node inf = main.nodes.get(0);
        //main.gui.addNode(new Node(3));
        //Node inf2 = main.nodes.get(new Random().nextInt(main.numNodes));
        
        
        new Thread(new Runnable(){
            @Override
            public void run(){
                try {
                    main.mutateNode(inf,main.tries);
                } catch (Exception ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
        new Thread(new Runnable(){
            @Override
            public void run(){
                //main.mutateNode(inf2,main.tries);
            }
        });
        
        new Thread(new Runnable(){
            @Override
            public void run(){
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
                List<Node> nds = new ArrayList<>();
                for(Node n: main.nodes){
                    if(!n.getOk()){
                        nds.add(n);
                    }
                }
                if(nds.size() == main.numNodes){
                    System.out.println("SUCCESSFUL INFECTION!");
                }else{
                    System.out.println("NOT SUCCESSFUL!");
                }
                System.out.println("INFECTED: " + nds.size() + "\n NODES:" + nds);


            }
        });
        
        
        
        
        //System.out.println(nodes);
    }
}