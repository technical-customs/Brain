package node;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NodeConnection{
    //will make it for many connections or limited amount of nodes
    private Node a, b;
    private volatile boolean ok;
    public Color connectionColor = Color.white;
    private int weight;
  
    public static List<NodeConnection> connections = new ArrayList<>();
    
    public NodeConnection(Node a, Node b){
        this.a = a;
        this.b = b;
        ok = true;
    }
    
    public Node getA(){
        return a;
    }
    public void setA(Node a){
        this.a = a;
    }
    public Node getB(){
        return b;
    }
    public void setB(Node b){
        this.b = b;
    }
    
    public void setWeight(int weight){
        this.weight = weight;
    }
    public int getWeight(){
        return weight;
    }
    
    public void setOk(boolean ok){
        this.ok = ok;
    }
    public boolean getOk(){
        return ok;
    }
    
    public void checkOk(){
        if(!a.getOk() && !b.getOk()){
            ok = false;
        }else{
            ok = true;
        }
            
    }
    public boolean checkConnection(Node a, Node b){
        return (this.a.equals(a) && this.b.equals(b)) || (this.a.equals(b) && this.b.equals(a));    
    }
        
    
    private synchronized Color getConnectionColor(){
        final CountDownLatch latch = new CountDownLatch(1);
        final Color[] color = new Color[1];
        //if(a != null && b != null){
        new Thread(new Runnable(){
            @Override
            public void run(){
            if(!ok && ( (a.infecting && !a.getOk()) || (b.infecting && !b.getOk()) )){
                //ok = true;
                //color[0] = getErrorColor();
                //ok = false;
            }else if(!ok || ( (!a.infecting && !a.getOk()   ) &&  (!b.infecting && !b.getOk()   ) )){
                //ok = false;
                //color[0] =  Color.red;
                //ok = false;
            }else if(ok){
                //color[0] =  Color.green;
                //ok = false;
            }
            
            color[0] = (ok) ? Color.green : Color.red ;
            latch.countDown();
        //}
        }
        }).start();
        
        try {
            latch.await();
            connectionColor = color[0];
        } catch (InterruptedException ex) {
            Logger.getLogger(NodeConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return connectionColor;
    }
    private Color tmpC = Color.red;
    private Color getErrorColor(){
        final Color c = tmpC;
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
            Thread.sleep(10);
            return tmpC;
            
        } catch (InterruptedException ex) {
            Logger.getLogger(NodeConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tmpC;
    }
    
    
    
    public synchronized void drawNodeConnection(Graphics2D graphics){
        
        graphics.setColor(getConnectionColor());
        
        
        
        if(a != null && b != null){
            int ahw =  a.getNodeLink().getX()+(10/2);
            int ahh =  a.getNodeLink().getY()+(10/2);
                
            int bhw = b.getNodeLink().getX()+(10/2);
            int bhh = b.getNodeLink().getY()+(10/2);
            
            graphics.drawLine(ahw,ahh,bhw,bhh);
        }
    }
    
    @Override
    public boolean equals(Object obj){
        if (obj == null) {
            return false;
        }
        if (!NodeConnection.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final NodeConnection other = (NodeConnection) obj;
        
        return (this.a.equals(other.getA()) && this.b.equals(other.getB()))
                ||(this.a.equals(other.getB()) && this.b.equals(other.getA()));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.a);
        hash = 73 * hash + Objects.hashCode(this.b);
        hash = 73 * hash + (this.ok ? 1 : 0);
        hash = 73 * hash + this.weight;
        return hash;
    }
    
    @Override
    public String toString(){
        return "NODE:" + a.getId() + " - NODE:" + b.getId();
    }
}
