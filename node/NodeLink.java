package node;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

public class NodeLink {
    private Node node;
    private Color nodeColor = Color.yellow;
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
    
    private boolean nodeBattle(NodeConnection nc, Node n){
        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] bool = new boolean[1];
        new Thread(new Runnable(){
            @Override
            public void run(){
                
                boolean win = false;
                
                int w = 0;
                do{
                    
                    try {
                        Thread.sleep(100);
                    } catch (Exception ex) {}
                    
                    node.setWeight(new Random().nextInt());
                    n.setWeight(new Random().nextInt());
                    int a = node.getWeight();
                    int b = n.getWeight();
                    
                    if(a > b){
                        win = true;
                        //n.setOk(false);
                        
                    }if(a < b){
                        win = false;
                        //n.setOk(true);
                    }
                    w++;
                        
                }while(w < n.diff);
                
                try {
                    //Thread.sleep(1000);
                } catch (Exception ex) {}
                if(win){
                    infected.add(n);
                    n.getNodeLink().getInfected().add(node);
                    
                    //nc.setOk(false);
                    bool[0] = true;
                }else{
                    //fight back
                    bool[0] = false;
                    //nc.setOk(true);
                }
                nc.checkOk();
                latch.countDown();
            }
        }).start();
        try {
            latch.await();
            Thread.sleep(100);
            //System.out.println("NODE BATTLE RESULT: " + bool[0]);
            return bool[0];
        } catch (InterruptedException ex) {
            return false;
        }
        
    }
    
    private void fightInfection(Node node){
        if(!node.getOk()){
            //fight random connection
            int rn = new Random().nextInt(node.getConnected().size());
            boolean pass = false;
            
            do{
                Node n = (Node) node.getConnected().get(rn);
                if(!n.getOk()){
                    for(Object obj: node.getConnections()){
                        NodeConnection nc = (NodeConnection)obj;
                        
                        if(nc.checkConnection(node, n) && !nc.getOk()){
                            //fight this node and connection
                            //sever connection or take over computer, put in 
                            //list of severed connections
                        }
                    }
                }
            }while(!pass);
            
        }
    }
    
    public void infectLink(Node n){
        //all connections
        for(Object obj: node.getConnections()){
            NodeConnection nc = (NodeConnection) obj;
            
            if(!nc.getOk()){
                continue;
            }
            
            if(nc.getA().equals(n)){
                if(nc.getA().getOk()){
                    //nc.setOk(false);
                    if(nc.getA().getConnection(nc.getB()) != null){
                        nc.getA().getConnection(nc.getB()).setOk(false);
                    }else{
                        nc.getB().getConnection(nc.getA()).setOk(false);
                    }
                    
                    nc.setOk(false);
                    nc.getA().setOk(false);
                    nc.getA().infecting = true;
                    
                    boolean b = nodeBattle(nc,nc.getA());
                    
                    if(nc.getA().getConnection(nc.getB()) != null){
                        nc.getA().getConnection(nc.getB()).setOk(!b);
                    }else{
                        nc.getB().getConnection(nc.getA()).setOk(!b);
                    }
                    nc.setOk(!b);
                    nc.getA().setOk(!b);
                    nc.getA().infecting = false;
                    break;
                }
            }
            if(nc.getB().equals(n)){
                if(nc.getB().getOk()){
                    //nc.setOk(false);
                    if(nc.getB().getConnection(nc.getA()) != null){
                        nc.getB().getConnection(nc.getA()).setOk(false);
                    }else{
                        nc.getA().getConnection(nc.getB()).setOk(false);
                    }
                    
                    nc.setOk(false);
                    nc.getB().setOk(false);
                    nc.getB().infecting = true;
                    
                    boolean b = nodeBattle(nc,nc.getB());
                    
                    if(nc.getB().getConnection(nc.getA()) != null){
                        nc.getB().getConnection(nc.getA()).setOk(!b);
                    }else{
                        nc.getA().getConnection(nc.getB()).setOk(!b);
                    }
                    nc.setOk(!b);
                    nc.getB().setOk(!b);
                    nc.getB().infecting = false;
                    
                    break;
                }
            }
        }
    }
    
    
    private Color getNodeColor(){
        final CountDownLatch latch = new CountDownLatch(1);
        final Color[] color = new Color[1];
        
        new Thread(new Runnable(){
            @Override
            public void run(){
                if(!node.infecting && !node.getOk()){
                    //ok = false;
                    color[0] = Color.red;
                    //ok = false;
                }if(!node.getOk() && node.infecting ){
                    //ok = true;
                    color[0] = Color.red;
                }else if(node.getOk()){
                    color[0] = Color.yellow;
                }
                latch.countDown();
            }
        }).start();
        try {
            latch.await();
            
            nodeColor = color[0];
        } catch (InterruptedException ex) {
            
        }
        
        return nodeColor;
    }
    private Color tmpC = Color.red;
    private Color getErrorColor(){
        final CountDownLatch latch = new CountDownLatch(1);
        final Color[] color = new Color[1];
        
        new Thread(new Runnable(){
            @Override
            public void run(){
                
                
                if(tmpC.equals(Color.red)){
                    color[0] = Color.white;
                    
                }
                if(tmpC.equals(Color.white)){
                    color[0] = Color.red;
                    
                }
                try {
                    //Thread.sleep(10);
                } catch (Exception ex) {}
                latch.countDown();
            }
        }).start();
        
        try {
            latch.await();
            tmpC = color[0];
            Thread.sleep(100);
            return tmpC;
            
        } catch (InterruptedException ex) {
            Logger.getLogger(NodeConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tmpC;
    }
    public synchronized void drawNodeLink(Graphics2D graphics){
        
        graphics.setColor(getNodeColor());
        
        graphics.fillOval(x, y, 10, 10);
        
        graphics.setFont(new Font(Font.SERIF,0,10));
        graphics.drawString("NODE:"+node.getId(), x, y);
        
      
    }
    
    
}