package main;

import gui.Gui;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import node.Node;
import node.NodeConnection;

class Main {
    private int numNodes = 12;
    private List<Node> nodes = new ArrayList<>();
    private List<NodeConnection> connections = new ArrayList<>();
    private Gui gui;
    
    public Main(){
        gui = new Gui();
        gui.start();
        
        //main nodes
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
        
        /*
        //make random connections
        for(int x = 0; x < nodes.size(); x++){
            boolean gotNode = false;
            Node n = null;
            do{
                boolean all = true;
                for(Node node: nodes){
                    if(node.getConnected().isEmpty()){
                        all = false;
                    }
                }
                if(all){
                    break;
                }
                
                int r = new Random().nextInt(nodes.size());
                if(r != x && nodes.get(r).getConnected().isEmpty()){
                    n = nodes.get(r);
                    gotNode = true;
                }
            }while(!gotNode);
            
            if(nodes.get(x).getConnected().isEmpty()){
                nodes.get(x).addConnected(n);
            }
        }
        
        //two connections
        for(int x = 0; x < nodes.size(); x++){
            boolean gotNode = false;
            Node n = null;
            do{
                boolean all = true;
                for(Node node: nodes){
                    if(node.getConnected().size() < 2){
                        all = false;
                    }
                }
                if(all){
                    break;
                }
                
                int r = new Random().nextInt(nodes.size());
                if(r != x && nodes.get(r).getConnected().size() == 1 &&
                        !nodes.get(x).getConnected().contains(nodes.get(r))){
                    n = nodes.get(r);
                    gotNode = true;
                }
            }while(!gotNode);
            
            if(nodes.get(x).getConnected().size() == 1){
                nodes.get(x).addConnected(n);
            }
        }
        
        */
        
        //makeConnections(1);
        makeConnections(3);
        
        
        System.out.println("Nodes: " + nodes.size());
        System.out.println("Connections: " + connections.size());
    }
    
    private void makeConnections(int num){
        //num *= 2;
        if(num > nodes.size()-1 || nodes.size() % num != 0){
           return;
        }
        
        for(int y = 1; y < num+1; y++){
            int yy = y - 1;
            System.out.println("Y="+y);
            
            for(int x = 0; x < nodes.size(); x++){
                System.out.println("X="+x);
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
                    
                    System.out.println("GOTNODE-"+gotNode + " - " + n);
                }while(!gotNode);

                if(nodes.get(x).getConnected().size() == yy){
                    System.out.println("CONNECTING NODES:" + nodes.get(x) + " NODE:" + n.getId());
                    nodes.get(x).addConnected(n);
                    
                    
                }
                //y++;
                //System.out.println("Y="+y);
            }
        }
        
        connections.clear();
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
    
    
    public static void main(String[] args) throws InterruptedException{
        Main main = new Main();
        int tries = main.nodes.size()*2;
        
        Thread.sleep(1000);
        Node inf = main.nodes.get(new Random().nextInt(main.numNodes));
        Thread.sleep(1000);
        
        inf.setOk(false);
        Thread.sleep(1000);
        
        List<Node> infectednodes = new ArrayList<>();
        int numOfInfected = 0;
        Node in = null;
        
        
        boolean success = false;
        for(int x = 0; x < tries;){
            if(numOfInfected >= main.nodes.size()-1){
                success = true;
                break;
            }
            if(in == null){
                in = inf;
            }
            if(!infectednodes.contains(in)){
                infectednodes.add(in);
            }
            
            
            boolean pass = false;
            int rc = 0;
            int sleep = 1;
            do{
                //get random connection from in
                rc = new Random().nextInt(in.getConnections().size());
                NodeConnection rcn = (NodeConnection) in.getConnections().get(rc);
                
                
                
                //link not ok
                if(!rcn.getOk() || (!rcn.getA().getOk() && !rcn.getB().getOk())){
                    //System.out.println("NETWORK INFECTED");
                    //in = in.getNodeLink().getInfected().get(in.getNodeLink().getInfected().size()-1);
                    x++;
                    break;
                }
                if(!rcn.getOk() && (!rcn.getA().getOk() || !rcn.getB().getOk())){
                    //System.out.println("NETWORK INFECTED");
                    in = in.getNodeLink().getInfected().get(in.getNodeLink().getInfected().size()-1);
                    x++;
                    continue;
                }
                
                Node rn = (Node) in.getConnected().get(rc);
                if(rn.getOk()){
                    in.getNodeLink().infectLink(rn);
                    sleep = rn.diff;
                    pass = true;
                }
                
                x++;
            }while(!pass);
            
            Thread.sleep((100*sleep)+sleep);
            
            int is = in.getNodeLink().getInfected().size();
            
            x++;
            if(numOfInfected != is){
                in = in.getNodeLink().getInfected().get(in.getNodeLink().getInfected().size()-1); 
            }
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
        
        
        
        //System.out.println(nodes);
    }
}