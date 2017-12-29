package node;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Node<T extends Comparable<T>>{
    /*
    * Node for Linked Lists, Search Trees,
    */
    
    public int diff = new Random().nextInt(80)+20;//difficulty of node easy = 50 , hard = 500
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
    public boolean infecting;
    private NodeLink nl;
    
    public Node(int id){
        connected = new ArrayList<>();
        connections = new ArrayList<>();
        this.id = id;
        ok = true;
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
                for(NodeConnection nc: connections){
                    if(nc.checkConnection(n, nc.getA()) || nc.checkConnection(n, nc.getB())){
                        if(nc.getA().getOk() && nc.getB().getOk()){
                            ok = false;
                        }else if( (!nc.getA().getOk() && nc.getB().getOk()) || (nc.getA().getOk() && !nc.getB().getOk()) ){
                            ok = true;
                        }
                    }
                }
                //return true;
            //}
        }
        //return false;
    }
    
    public NodeLink getNodeLink(){
        return nl;
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