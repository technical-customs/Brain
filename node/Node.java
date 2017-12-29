package node;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Node<T extends Comparable<T>>{
    /*
    * Node for Linked Lists, Search Trees,
    */
    public int HIGHDIF = 100, LOWDIF = 20;
    public int diff = new Random().nextInt(HIGHDIF - LOWDIF)+LOWDIF;//difficulty of node easy = 50 , hard = 500
    private int weight = new Random().nextInt();
    private T value = null;
    private Node<T> prev = null;
    private Node<T> next = null;
    private int index;
    
    private int height;
    
    public int balance;
    
    private Node<T> parent = null;
    private Node<T> leftChild = null;
    private Node<T> rightChild = null;
    
    private Color nodeColor = Color.white;
    private List<Node> connected;
    private List<NodeConnection> connections;
    private int id ;
    private boolean ok;
    
    public volatile boolean infecting;
    public volatile boolean recovering;
    public volatile boolean recovered;
    
    private NodeLink nl;
    
    public Node(int id){
        connected = new ArrayList<>();
        connections = new ArrayList<>();
        this.id = id;
        ok = true;
        infecting = false;
        recovering = false;
        recovered = false;
        
        nl = new NodeLink(this);
        
    }
    
    public void randColor(){
        nodeColor = new Color(new Random().nextInt(255),new Random().nextInt(255),new Random().nextInt(255));
    }
    public void setNodeColor(Color nodeColor){
        this.nodeColor = nodeColor;
    }
    public Color getNodeColor(){
        return nodeColor;
    }
    public int getWeight(){
        return weight;
    }
    public void setWeight(int weight){
        this.weight = weight;
    }
    public int getId(){
        return id;
    }
    public boolean getOk(){
        return ok;
    }
    public void setOk(boolean ok){
        this.ok = ok;
    }
    public List<Node> getConnected(){
        return connected;
    }
    public void addConnected(Node node){
        if(!connected.contains(node)){
            connected.add(node);
            node.addConnected(this);
            
            NodeConnection nc = new NodeConnection(this,node);
            if(!connections.contains(nc)){
               connections.add(nc);
               NodeConnection.connections.add(nc);
            }
            
            
            
        }
    }
    public void removeConnected(Node node){
        if(connected.contains(node)){
            connected.remove(node);
            node.removeConnected(this);
            
        }
    }
    
    public List<NodeConnection> getConnections(){
        return connections;
    }
    public NodeConnection getConnection(Node node){
        for(NodeConnection nc: connections){
            if(nc.checkConnection(this, node)){
                return nc;
            }
        }
        return null;
    }
    public void addConnection(NodeConnection connection){
        if(!connections.contains(connection)){
            connections.add(connection);
        }
    }
    public void removeConnection(NodeConnection connection){
        if(connections.contains(connection)){
            connections.remove(connection);
        }
    }
    public void checkConnections(){
        for(Node n: connected){
            //if(node.equals(n)){
            if(connections.contains(getConnection(n))){
                NodeConnection nc = n.getConnection(this);
                    
                
                if(nc.getA().getOk() && nc.getB().getOk()){
                    ok = false;
                }else if( (!nc.getA().getOk() && nc.getB().getOk()) || (nc.getA().getOk() && !nc.getB().getOk()) ){
                    ok = true;
                }
                if(nc.getA().recovered && nc.getB().recovered){
                    nc.setOk(true);
                }
                
                
            }else if(n.getConnections().contains(this)){
                NodeConnection nc = n.getConnection(this);
                
                if(nc.getA().getOk() && nc.getB().getOk()){
                    ok = false;
                }else if( (!nc.getA().getOk() && nc.getB().getOk()) || (nc.getA().getOk() && !nc.getB().getOk()) ){
                    ok = true;
                }else if(nc.getA().recovered && nc.getB().recovered){
                    nc.setOk(true);
                }
            }
            
                
                //return true;
            //}
        }
        //return false;
    }
    public boolean allConnectionsOk(){
        List<NodeConnection> infected = new ArrayList<>();
        for(NodeConnection nc: connections){
            if(!nc.getOk()){
                infected.add(nc);
            }
        }
        return connections.size() != infected.size();
    }
    
    
    
    
    public NodeLink getNodeLink(){
        return nl;
    }
    
    public void setRecovering(boolean recovering){
        this.recovering = recovering;
    }
    public void setRecovered(boolean recovered){
        this.recovered = recovered;
        ok = recovered;
        infecting = false;
        
        checkConnections();
    }
    
    public void setValue(T value){
        this.value = value;
    }
    public T getValue(){
        return this.value;
    }
    
    
    public void setParent(Node<T> parent){
        this.parent = parent;
    }
    public Node<T> getParent(){
        return this.parent;
    }
    public void setLeftChild(Node<T> leftChild){
        this.leftChild = leftChild;
        leftChild.setParent(this);
    }
    public Node<T> getLeftChild(){
        return this.leftChild;
    }
    public void setRightChild(Node<T> rightChild){
        this.rightChild = rightChild;
        rightChild.setParent(this);
    }
    public Node<T> getRightChild(){
        return this.rightChild;
    }
    
    @Override
    public boolean equals(Object obj){
        if (obj == null) {
            return false;
        }
        if (!Node.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final Node other = (Node) obj;
        /*
        if(!value.equals(other.value)){
            return false;
        }
        if(!prev.equals(other.prev)){
            return false;
        }
        if(!next.equals(other.next)){
            return false;
        }
        if(index != other.index){
            return false;
        }
        if(height != other.height){
            return false;
        }
        if(balance != other.balance){
            return false;
        }
        if(!parent.equals(other.parent)){
            return false;
        }
        if(!leftChild.equals(other.leftChild)){
            return false;
        }
        */
        return id == other.id;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.value);
        hash = 79 * hash + Objects.hashCode(this.prev);
        hash = 79 * hash + Objects.hashCode(this.next);
        hash = 79 * hash + this.index;
        hash = 79 * hash + this.height;
        hash = 79 * hash + this.balance;
        hash = 79 * hash + Objects.hashCode(this.parent);
        hash = 79 * hash + Objects.hashCode(this.leftChild);
        hash = 79 * hash + Objects.hashCode(this.rightChild);
        return hash;
    }
    @Override
    public String toString(){
        return "Node:" + id + "\n";
    }
}