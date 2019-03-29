/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codigo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.Timer;

/**
 *
 * @author Sergio Garcia
 */
public class VentanaJuego extends javax.swing.JFrame {

    static int ANCHOPANTALLA = 800;
    static int ALTOPANTALLA = 700;

    //numero de marcianos que van a aparecer
    int filas = 7;
    int columnas = 7;

    BufferedImage buffer = null;

    Nave miNave = new Nave();
    Disparo miDisparo = new Disparo();
    //Marciano miMarciano = new Marciano();
    Marciano[][] listaMarcianos = new Marciano[filas][columnas];
    boolean direccionMarcianos = false;
    //el contador sirve para decidir qué imagen del marciano toca poner
    int contador = 0;
    ArrayList <Explosion> listaExplosiones = new ArrayList();
    //imagen para cargar el spritesheet con todos los sprites del juego
    BufferedImage plantilla = null;
    Image [][] imagenes ;
    
    
    Timer temporizador = new Timer(6, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            bucleDelJuego();
        }
    });

    /**
     * Creates new form VentanaJuego
     */
    public VentanaJuego() {
        initComponents();
        //para cargar el archivo de imagenes
        // 1º ruta del archivo
        // 2º filas
        // 3º columnas
        // 4º ancho
        // 5º alto
        //6º escala
        imagenes=cargaImagenes("/imagenes/soldados+.png",10,10,32,32,1);
       // miDisparo.imagen=imagenes[2][2];
        
        setSize(ANCHOPANTALLA, ALTOPANTALLA);
       
        
        buffer = (BufferedImage) jPanel1.createImage(ANCHOPANTALLA, ALTOPANTALLA);
        buffer.createGraphics();
          Icon imgBoton=new ImageIcon(getClass().getResource("/imagenes/infierno.jpg"));

        temporizador.start();
        
        boolean gameOver=false;

        //inicializo la posición inicial de la nave
        //miNave.imagen = imagenes[2][1];
        miNave.x = ANCHOPANTALLA / 2 - miNave.imagen.getWidth(this) / 2;
        miNave.y = ALTOPANTALLA - miNave.imagen.getHeight(this) - 40;
        
        
        //inicializo el array de marcianos
        //1º parametro fila que estoy creando
        // 2º fila del sprite sheet que quiero seleccionar
        // 3º columna del sprite sheet que quiero seleccionar
         creaFilaDeMarcianos(0, 0 ,0);
         creaFilaDeMarcianos(1, 1 ,0);
         creaFilaDeMarcianos(2, 2 ,0);
         creaFilaDeMarcianos(3, 3 ,0);
         creaFilaDeMarcianos(4, 4 ,0);
         creaFilaDeMarcianos(5, 5 ,0);
         creaFilaDeMarcianos(6, 6 ,0);
         
        
        
    }
    private void creaFilaDeMarcianos(int numeroFila, int spriteFila, int spriteColumna){
         for (int j = 0; j < columnas; j++) {
                listaMarcianos[numeroFila][j] = new Marciano();
                listaMarcianos[numeroFila][j].imagen1 = imagenes[spriteFila][spriteColumna];
                listaMarcianos[numeroFila][j].imagen2 = imagenes[spriteFila][spriteColumna];
                listaMarcianos[numeroFila][j].x = j * (15 + listaMarcianos[numeroFila][j].imagen1.getWidth(null));
                listaMarcianos[numeroFila][j].y = numeroFila * (10 + listaMarcianos[numeroFila][j].imagen1.getHeight(null));
            }
    }
    /*
    este metodo va a servir para crear el array de imagenes con todas las imagenes del spritesheet.
    Devolvera un array de dos dimesiones con las imagenes colocadas tal y como estan en el 
    spritesheet
    */  
    private Image[][] cargaImagenes(String nombreArchivoImagenes, int numFilas,int numColumnas,int ancho,int alto,int escala){
        try {
           plantilla = ImageIO.read(getClass().getResource(nombreArchivoImagenes));
          
        } catch (IOException ex) {            
        }
        Image[][] arrayImagenes = new Image[numFilas+1][numColumnas+1];
        
        //cargo las imagenes de forma individual en cada imagen del array de imagenes
        for (int i=0; i<numFilas; i++){
            for (int j=0; j<numColumnas; j++){
            arrayImagenes[i][j] = plantilla.getSubimage(j*ancho, i*alto, ancho ,alto);
            arrayImagenes[i][j] =  arrayImagenes[i][j].getScaledInstance(ancho/escala,ancho/escala, Image.SCALE_SMOOTH);
            }
        }
        //cargo
       for (int i=0; i<numFilas; i++){
            for (int j=0; j<numColumnas; j++){
            arrayImagenes[numFilas][j] = plantilla.getSubimage(j*ancho, i*alto, ancho ,alto/2);
            arrayImagenes[numFilas][j] =  arrayImagenes[i][j].getScaledInstance(2*ancho/escala,ancho/escala, Image.SCALE_SMOOTH);
            }
       }
         for (int i=0; i<numColumnas; i++){
            arrayImagenes[i][numColumnas] = plantilla.getSubimage(numColumnas*ancho, i*alto, ancho ,alto);
            arrayImagenes[i][numColumnas] =  arrayImagenes[i][numColumnas].getScaledInstance(ancho/escala/2,ancho/escala, Image.SCALE_SMOOTH);
            }
        
       
        return arrayImagenes;
    }
    
    

    private void bucleDelJuego() {
        //se encarga del redibujado de los objetos en el jPanel1
        //primero borro todo lo que hay en el buffer
        contador++;
        Graphics2D g2 = (Graphics2D) buffer.getGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, ANCHOPANTALLA, ALTOPANTALLA);

        ///////////////////////////////////////////////////////
        //redibujaremos aquí cada elemento
        g2.drawImage(miDisparo.imagen, miDisparo.x, miDisparo.y, null);
        g2.drawImage(miNave.imagen, miNave.x, miNave.y, null);
        pintaMarcianos(g2);
        chequeaColision();
        miNave.mueve();
        miDisparo.mueve();
        /////////////////////////////////////////////////////////////
        //*****************   fase final, se dibuja ***************//
        //*****************   el buffer de golpe sobre el Jpanel***//

        g2 = (Graphics2D) jPanel1.getGraphics();
        g2.drawImage(buffer, 0, 0, null);

    }
    


    private void chequeaColision(){
        Rectangle2D.Double rectanguloMarciano = new Rectangle2D.Double();
        Rectangle2D.Double rectanguloDisparo = new Rectangle2D.Double();
        
        rectanguloDisparo.setFrame( miDisparo.x, 
                                    miDisparo.y,
                                    miDisparo.imagen.getWidth(null),
                                    miDisparo.imagen.getHeight(null));
        
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (listaMarcianos[i][j].vivo) {
                    rectanguloMarciano.setFrame(listaMarcianos[i][j].x,
                                                listaMarcianos[i][j].y,
                                                listaMarcianos[i][j].imagen1.getWidth(null),
                                                listaMarcianos[i][j].imagen1.getHeight(null)
                                                );
                    if (rectanguloDisparo.intersects(rectanguloMarciano)){
                        listaMarcianos[i][j].vivo = false;
                        miDisparo.posicionaDisparo(miNave);
                        miDisparo.y = 1000;
                        miDisparo.disparado = false;
                    }
                }
            }
        }
    }
    private void pintaExplosiones( Graphics2D g2){
            //pinto las explosiones
        for (int i=0; i<listaExplosiones.size(); i++){
            Explosion e = listaExplosiones.get(i);
            e.setTiempoDeVida(e.getTiempoDeVida() - 1);
            if (e.getTiempoDeVida() > 25){
                g2.drawImage(e.imagenExplosion, e.getX(), e.getY(), null);
            }
            else {
                g2.drawImage(e.imagenExplosion2, e.getX(), e.getY(), null);
            }
            
             //si el tiempo de vida de la explosión es menor que 0 la elimino
            if (e.getTiempoDeVida() <= 0){
                listaExplosiones.remove(i);
            }
        }
}
    private void reproduce (String cancion){
           try {
            Clip sonido = AudioSystem.getClip();
            sonido.open(AudioSystem.getAudioInputStream( getClass().getResource(cancion) ));
            sonido.loop(0);
        } catch (Exception e) {      
        } 
    }
    private void finDePartida (Graphics2D ramon){
        try {
            Image imagenLuser = ImageIO.read((getClass().getResource("/imagenes/heroecondon.png")));
            ramon.drawImage(imagenLuser, 100, 100, null);
        } catch (IOException ex) {
        }
}
    private void cambiaDireccionMarcianos() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
              
                listaMarcianos[i][j].setvX(listaMarcianos[i][j].getvX()* -1);
            }
        }
    }
    
    
    private void pintaMarcianos(Graphics2D _g2) {

        int anchoMarciano = listaMarcianos[0][0].imagen1.getWidth(null);
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (listaMarcianos[i][j].vivo) {
                    listaMarcianos[i][j].mueve();
                    //chequeo si el marciano ha chocado contra la pared para cambiar la dirección 
                    //de todos los marcianos
                    if (listaMarcianos[i][j].x + anchoMarciano == ANCHOPANTALLA || listaMarcianos[i][j].x == 0) {
                        direccionMarcianos = true;
                    }
                    if (contador < 0) {
                        _g2.drawImage(listaMarcianos[i][j].imagen1,
                                listaMarcianos[i][j].x,
                                listaMarcianos[i][j].y,
                                null);
                    } else if (contador < 50) {
                        _g2.drawImage(listaMarcianos[i][j].imagen2,
                                listaMarcianos[i][j].x,
                                listaMarcianos[i][j].y,
                                null);
                    } 
                    else {
                        contador = 0;
                    }
                }
            }
        }
        if (direccionMarcianos ){
            cambiaDireccionMarcianos();
            direccionMarcianos = false;
        }
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });

        jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel1MousePressed(evt);
            }
        });

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/infierno.jpg"))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(132, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(105, 105, 105))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(184, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed

        switch (evt.getKeyCode()) {
            
            case KeyEvent.VK_LEFT:
                miNave.setPulsadoIzquierda(true);
                
                break;
            case KeyEvent.VK_RIGHT:
                miNave.setPulsadoDerecha(true);
              
                break;
            case KeyEvent.VK_SPACE:
                miDisparo.posicionaDisparo(miNave);
                miDisparo.disparado = true;
           
                 reproduce ("/sonidos/yvolo.wav") ;
                 
                }
    
    
        
    }//GEN-LAST:event_formKeyPressed

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                miNave.setPulsadoIzquierda(false);
                break;
            case KeyEvent.VK_RIGHT:
                miNave.setPulsadoDerecha(false);
                break;
               case KeyEvent.VK_SPACE:
                miDisparo.posicionaDisparo(miNave);
                miDisparo.disparado = true;
                 //reproduce ("/sonidos/yvolo2.wav") ;
        }
    }//GEN-LAST:event_formKeyReleased

    private void jPanel1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MousePressed
              

    }//GEN-LAST:event_jPanel1MousePressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaJuego().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
