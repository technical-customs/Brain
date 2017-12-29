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
    private int x,y,z;

    public NodeLink(Node node){
        this.node = node;
        
    }
    
    public Node getNode() {
        return node;
    }
    public void setNode(Node node) {
        this.node = node;
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
                        Thread.sleep(50);
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
                    //infected.add(n);
                    //n.getNodeLink().getInfected().add(node);
                    
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
            Thread.sleep(50);
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
    
    public boolean infectLink(Node n){
        //all connections
        for(Object obj: node.getConnections()){
            NodeConnection nc = (NodeConnection) obj;
            
            if(!nc.getOk()){
                continue;
            }
            
            if(nc.getA().equals(n)){
                if(nc.getA().getOk()){
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
                    return b;
                }
            }
            if(nc.getB().equals(n)){
                if(nc.getB().getOk()){
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
                    
                    return b;
                }
            }
        }
        return false;
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
                }else if(!node.getOk() && node.infecting && !node.recovered && !node.recovering){
                    //ok = true;
                    color[0] = getErrorColor();
                }else if(node.getOk()){
                    color[0] = Color.yellow;
                }
                if(node.recovering && !node.recovered){
                    color[0] = getRecoveryColor();
                }else if(!node.recovering && node.recovered){
                    color[0] = Color.blue;
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
            //Thread.sleep(100);
            return tmpC;
            
        } catch (InterruptedException ex) {
            Logger.getLogger(NodeConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tmpC;
    }
    
    private Color recC = Color.blue;
    private Color getRecoveryColor(){
        final CountDownLatch latch = new CountDownLatch(1);
        final Color[] color = new Color[1];
        
        new Thread(new Runnable(){
            @Override
            public void run(){
                
                
                if(recC.equals(Color.blue)){
                    color[0] = Color.white;
                    
                }
                if(recC.equals(Color.white)){
                    color[0] = Color.blue;
                    
                }
                try {
                    //Thread.sleep(10);
                } catch (Exception ex) {}
                latch.countDown();
            }
        }).start();
        
        try {
            latch.await();
            recC = color[0];
            //Thread.sleep(100);
            return recC;
            
        } catch (InterruptedException ex) {}
        return recC;
    }
    public synchronized void drawNodeLink(Graphics2D graphics){
        
        graphics.setColor(getNodeColor());
        
        graphics.fillOval(x, y, 10, 10);
        
        graphics.setFont(new Font(Font.SERIF,0,10));
        graphics.drawString("NODE:"+node.getId(), x, y);
        
      
    }
    
    
}