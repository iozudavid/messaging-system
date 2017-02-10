import java.io.*;

class HidePassword implements Runnable {
   
	private boolean finish;
   
   public void run () {
      finish = true;
      while (finish) {
         System.out.print("\b*");
     try {
        Thread.currentThread().sleep(1);
         } catch(InterruptedException ie) {
            ie.printStackTrace();
         }
      }
   }

   public void stopHiding() {
      this.finish = false;
   }
}