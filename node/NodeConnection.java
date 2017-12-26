package node;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NodeConnection{
    //will make it for many connections or limited amount of nodes
    private Node a, b;
    private boolean ok;
    public Color connectionColor = Color.white;
    private int weight;
  
    public static List<NodeConnection> connections = new ArrayList<>();
    
    public NodeConnection(Node a, Node b){
        this.a = a;
        this.b = b;
        ok = true;
    }
    
    public synchronized Node getA(){
        return a;
    }
    public void setA(Node a){
        this.a = a;
    }
    public synchronized Node getB(){
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
    public synchronized boolean getOk(){
        return ok;
    }
    
    
    private Color getConnectionColor(){
        if(a != null && b != null){
            if(!a.getOk() && !b.getOk()){
                return Color.red;
            }
            return (ok) ? Color.green : Color.red ; 
        }
        return connectionColor;
    }
    public void drawNodeConnection(Graphics2D graphics){
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