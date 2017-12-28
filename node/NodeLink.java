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
                
                
                
                nc.setOk(false);
                int w = 0;
                do{
                    
                    try {
                        Thread.sleep(100);
                    } catch (Exception ex) {}
                    
                    node.setWeight(new Random().nextInt());
                    n.setWeight(new Random().nextInt());
                    int a = node.getWeight();
                    int b = n.getWeight();
                    
                    //System.out.println("INFECTED NODE: " + a);
                    //System.out.println("VICTIM NODE: " + b);
                    
                    if(a > b){
                        win = true;
                        n.setOk(false);
                        //nc.setOk(false);
                        //System.out.println("INFECTED NODE WINS");
                        
                    }if(a < b){
                        win = false;
                        n.setOk(true);
                        //nc.setOk(false);
                        //System.out.println("INFECTED NODE LOSES");
                    }
                    w++;
                        
                }while(w < n.diff);
                try {
                    //Thread.sleep(1000);
                } catch (Exception ex) {}
                if(win){
                    //System.out.println("NODE:" + n.getId() + " INFECTED");
                    infected.add(n);
                    n.getNodeLink().getInfected().add(node);
                    
                    nc.setOk(false);
                }else{
                    //fight back
                    nc.setOk(true);
                }
                nc.checkOk();
            }
        }).start();
        
    }
    
    public void infectLink(Node n){
        //all connections
        for(Object obj: node.getConnections()){
            NodeConnection nc = (NodeConnection) obj;
            
            if(!nc.getOk()){
                continue;
            }
            
            if(nc.getA().equals(n)){
                //link is not infected
                if(nc.getA().getOk()){
                    //nc.setInfectedStatus();
                    
                    nodeBattle(nc,nc.getA());
                    break;
                }
            }
            if(nc.getB().equals(n)){
                //link is not infected
                if(nc.getB().getOk()){
                    //nc.setInfectedStatus();
                    nodeBattle(nc,nc.getB());
                    break;
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