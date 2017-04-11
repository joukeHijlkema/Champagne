package Bulletin;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author hylkema
 */
class Pair {

  private int l;
  private int r;

  public Pair(int l, int r) {
    this.l = l;
    this.r = r;
  }

  public int getL() {
    return l;
  }

  public int getR() {
    return r;
  }

  public void setL(int l) {
    this.l = l;
  }

  public void setR(int r) {
    this.r = r;
  }

  int Span() {
    return this.r-this.l+1;
  }

  void Info() {
    System.out.println("l = " + l);
    System.out.println("r = " + r);
  }
}
