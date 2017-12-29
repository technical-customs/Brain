package gui;

import entities.Bot;
import entities.Entity;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import node.Node;
import node.NodeConnection;

public class Gui extends Screen{
    private List<Node> nodes;
    private List<NodeConnection> connections;
    
    public Gui() throws InterruptedException, InvocationTargetException{
        nodes = new ArrayList<>();
        connections = new ArrayList<>();
        SwingUtilities.invokeAndWait(new Runnable(){
            @Override
            public void run(){
                setupGUI();
            }
        });
    }
     private void setupGUI(){
        JFrame frame = new JFrame();
        
        setPreferredSize(SSIZE);
        setLayout(null);

        frame.getContentPane().add(this);
        frame.pack();
        frame.setResizable(false);
        frame.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent we){
                stop();
                System.exit(0);
            }
        });
        this.setDoubleBuffered(true);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
    }
    
    public void setNodes(List<Node> nodes){
        this.nodes.clear();
        this.nodes.addAll(nodes);
        
    }
    public synchronized void addNode(Node node){
        if(!nodes.contains(node)){
            nodes.add(node); 
        }
    }
    public void removeNode(Node node){
        if(nodes.contains(node)){
            nodes.remove(node);
        }
    }
    public void setConnections(List<NodeConnection> connections){
        this.connections = connections;
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
    
    
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        
        g2.setColor(Color.black);
        g2.fillRect(0, 0, SSIZE.width,SSIZE.height);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        
        nodedraw(g2);
        connectiondraw(g2);
        repaint();
    }
    
    private synchronized void nodedraw(Graphics2D g2){
        //game draws
        Iterator nIter = nodes.iterator();
        while(nIter.hasNext()){
            Node node = (Node) nIter.next();
            
            node.getNodeLink().drawNodeLink(g2);
        }
    }
    private synchronized void connectiondraw(Graphics2D g2){
        //game draws
        Iterator ncIter = connections.iterator();
        while(ncIter.hasNext()){
            NodeConnection nc = (NodeConnection) ncIter.next();
            
            nc.drawNodeConnection(g2);
        }
    }
    
}