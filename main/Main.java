package main;

import gui.Gui;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import node.Node;
import node.NodeConnection;

class Main {
    private int numCon = 3; 
    private int numNodes = 10;
    private int tries = 2;
    
    
    private List<Node> nodes = new ArrayList<>();
    private List<NodeConnection> connections = new ArrayList<>();
    private Gui gui;
    
    public Main(){
        gui = new Gui();
        gui.start();
                
        //tries = nodes.size()*(numNodes);
        setupNodes(numNodes);
        makeConnections(numCon);
    }
    private void setupNodes(int numNodes){
        for(int x = 0; x < numNodes; x++){
            Node node = new Node(x);
            
            int rx = new Random().nextInt(gui.SSIZE.width-20)+10;
            int ry = new Random().nextInt(gui.SSIZE.height-20)+10;
            
            node.getNodeLink().setX(rx);
            node.getNodeLink().setY(ry);
            nodes.add(node);
            //gui.addBot(node.getNodeLink().getX(), node.getNodeLink().getY(),10,10);
        }
        gui.setNodes(nodes);
    }
    private void makeConnections(int num){
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
    
    private void mutateNode(Node node, int tries){
        //Color randColor = new Color(new Random().nextInt(255),new Random().nextInt(255),new Random().nextInt(255));
        
        List<Node> infectednodes = new ArrayList<>();
        int numOfInfected = 0;
        node.setOk(false);
        node.randColor();
        Node in = null;
        
        
        boolean success = false;
        for(int x = 0; x < tries;){
            if(infectednodes.size() == nodes.size()){
                success = true;
                break;
            }
            if(in == null){
                in = node;
            }
            if(!infectednodes.contains(in)){
                infectednodes.add(in);
            }
            
            
            boolean pass = false;
            int rc = 0;
            int sleep = 1;
            do{
                //get random connection from in
                rc = new Random().nextInt(in.getConnected().size());
                Node rcn = (Node) in.getConnected().get(rc);
                
                
                
                //link not ok
                if(!rcn.getOk()){
                    x++;
                    continue;
                }
                
                //Node rn = (Node) in.getConnected().get(rc);
                
                if(rcn.getOk()){
                    in.getNodeLink().infectLink(rcn);
                    sleep = rcn.diff;
                    pass = true;
                    x++;
                }
            }while(!pass);
            
            try {
                Thread.sleep((100*sleep)+sleep);
            } catch (Exception ex) {}
            
            int is = in.getNodeLink().getInfected().size();
            
            if(numOfInfected != is){
                in = in.getNodeLink().getInfected().get(in.getNodeLink().getInfected().size()-1); 
            }
        }
        
        //break
    }
    
    public static void main(String[] args) throws InterruptedException{
        Main main = new Main();
        
        
        Thread.sleep(1000);
        Node inf = main.nodes.get(new Random().nextInt(main.numNodes));
        
        //Node inf2 = main.nodes.get(new Random().nextInt(main.numNodes));
        
       
        
        
        Thread.sleep(1000);
        
        new Thread(new Runnable(){
            @Override
            public void run(){
                main.mutateNode(inf,main.tries);
            }
        }).start();
        new Thread(new Runnable(){
            @Override
            public void run(){
                //main.mutateNode(inf2,main.tries);
            }
        });
        
        Thread.sleep(1000);
        
        //main.mutateNode(inf2,main.tries);
        
        //Thread.sleep(5000);
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
        }).start();
        
        
        
        
        //System.out.println(nodes);
    }
}