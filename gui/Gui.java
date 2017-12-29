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
    //SIZE
    //public final int SWIDTH = 300;
    //public final int SHEIGHT = SWIDTH / 16 * 9;
    //public int SSCALE = 3;
    //public Dimension SSIZE = new Dimension(SWIDTH * SSCALE, SHEIGHT * SSCALE);
    
    public static List<Entity> entities, deadEntities;
    
    private List<Node> nodes;
    private List<NodeConnection> connections;
    
    public Gui(){
        entities = new ArrayList();
        deadEntities = new ArrayList();
        nodes = new ArrayList<>();
        connections = new ArrayList<>();
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
        this.nodes = nodes;
        entities.clear();
        deadEntities.clear();
        
        for(Node n: nodes){
            entities.add(new Bot(this,n.getNodeLink().getX(),n.getNodeLink().getX(),10,10));
        }
        
    }
    public void addNode(Node node){
        if(!nodes.contains(node)){
            nodes.add(node);
            entities.add(new Bot(this,node.getNodeLink().getX(),node.getNodeLink().getX(),10,10));
            
        }
    }
    public void removeNode(Node node){
        if(nodes.contains(node)){
            nodes.remove(node);
            deadEntities.add(new Bot(this,node.getNodeLink().getX(),node.getNodeLink().getX(),10,10));
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
        //super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        
        g2.setColor(Color.black);
        g2.fillRect(0, 0, SSIZE.width,SSIZE.height);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        
        draw(g2);
        g2.dispose();
        repaint();
    }
    
    private void draw(Graphics2D g2){
        //game draws
        for(Node n: nodes){
            n.getNodeLink().drawNodeLink(g2);
        }
        for(NodeConnection nc: connections){
            nc.drawNodeConnection(g2);
        }
    }
}