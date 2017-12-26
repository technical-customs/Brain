package node;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

public class NodeLink {
    private Node node;
    private volatile List<Node> infected;
    private int x,y,z;

    public NodeLink(Node node){
        this.node = node;
        infected = new ArrayList<>();
    }
    
    public Node getNode() {
        return node;
    }
    public void setNode(Node node) {
        this.node = node;
    }

    public List<Node> getInfected(){
        return infected;
    }
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }
    public void setZ(int z) {
        this.z = z;
    }
    
    public void infectRandomLink(){
        //create list of nodes to infect
        List<NodeConnection> toInfect = new ArrayList<>();
        
        //get all non infected connections to node
        for(Object obj: node.getConnections()){
            NodeConnection connection = (NodeConnection) obj;
            
            Node a = connection.getA();
            Node b = connection.getB();
            
            if(!node.equals(a) && a.getOk()){
                toInfect.add(connection);
            }
            if(!node.equals(b) && b.getOk()){
                toInfect.add(connection);
            }
        }
        
        System.out.println("To Infect: " + toInfect);
        
        int ri = new Random().nextInt(toInfect.size());
        
        System.out.println("To Infect index: " + ri);
        
        if(node.getConnections().contains(toInfect.get(ri))){
            int i = node.getConnections().indexOf(toInfect.get(ri));
            System.out.println("Infecting: " + node.getConnections().get(i));
            
            NodeConnection nc = (NodeConnection) node.getConnections().get(i);
            //nc.setOk(false);
            
            Node nca = nc.getA();
            Node ncb = nc.getB();
            
            if(!nca.equals(node)){
                //nodeBattle(nc,nca);
            }else if(!ncb.equals(node)){
                //nodeBattle(nc,ncb);
            }
        }
        
    }
    
    private void nodeBattle(NodeConnection nc, Node n){
        new Thread(new Runnable(){
            @Override
            public void run(){
                boolean win = false;
                try {
                    //Thread.sleep(1000);
                } catch (Exception ex) {}
                
                for(int x = 0; x < n.diff; x++){
                    try {
                        Thread.sleep(100);
                    } catch (Exception ex) {}
                    
                    int a = node.getWeight();
                    int b = n.getWeight();
                    
                    //System.out.println("INFECTED NODE: " + a);
                    //System.out.println("VICTIM NODE: " + b);
                    
                    if(a > b){
                        win = true;
                        n.setOk(false);
                        //System.out.println("INFECTED NODE WINS");
                        
                    }if(a < b){
                        win = false;
                        n.setOk(true);
                        //System.out.println("INFECTED NODE LOSES");
                    }if(a == b){
                        
                    }
                    
                    
                    node.setWeight(new Random().nextInt());
                    n.setWeight(new Random().nextInt());
                }
                
                if(win){
                    //System.out.println("NODE:" + n.getId() + " INFECTED");
                    infected.add(n);
                    n.getNodeLink().getInfected().add(node);
                    
                    n.setOk(false);
                    nc.setOk(false);
                }else{
                    n.setOk(true);
                    nc.setOk(true);
                }
            }
        }).start();
        
    }
    
    public void infectLink(Node n){
        //all connections
        for(Object obj: node.getConnections()){
            NodeConnection nc = (NodeConnection) obj;
            
            if(nc.getA().equals(n)){
                nc.setOk(false);
                
                //link is not infected
                if(nc.getA().getOk()){
                    //init nodebattle
                    
                    //infect both ends
                    //link connection checking algorithm needed
                    nc.getA().getNodeLink().infectLink(node);
                    
                    nodeBattle(nc,nc.getA());
                }
            }
            if(nc.getB().equals(n)){
                nc.setOk(false);
                
                //link is not infected
                if(nc.getB().getOk()){
                    //init nodebattle
                    
                    //infect both ends
                    //link connection checking algorithm needed
                    nc.getB().getNodeLink().infectLink(node);
                    nodeBattle(nc,nc.getB());
                }
                
            }
        }
    }
    public void drawNodeLink(Graphics2D graphics){
        if(node.getOk()){
            graphics.setColor(Color.yellow);
        }else{
            graphics.setColor(Color.red);
            //get a random link and infect it
        }
        
        graphics.fillOval(x, y, 10, 10);
        
        graphics.setFont(new Font(Font.SERIF,0,10));
        graphics.drawString("NODE:"+node.getId(), x, y);
        
    }
    
    
}