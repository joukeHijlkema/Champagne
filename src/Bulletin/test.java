/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Bulletin;

import Champagne.dataBase;
import Champagne.Items.Periode;

/**
 *
 * @author hylkema
 */
public class test {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    // TODO code application logic here
    dataBase myDB = new dataBase("Champagne");
    Periode P = new Periode(1,2016,3);
    Bulletin3 b = new Bulletin3(P, 1, myDB);
    b.makePDF();
    b.Save("/tmp/test.pdf");
  }
  
}
