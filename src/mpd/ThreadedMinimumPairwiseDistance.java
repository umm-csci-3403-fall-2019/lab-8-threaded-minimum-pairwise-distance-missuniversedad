package mpd;

public class ThreadedMinimumPairwiseDistance implements MinimumPairwiseDistance, Runnable {
    int numThreads = 4;
    int quadrant;
    Answer answer;
    int[] values;
    int bestSoFar = Integer.MAX_VALUE;

    private ThreadedMinimumPairwiseDistance(int[] values, Answer answer, int quad){
        this.values = values;
        this.answer = answer;
        this.quadrant = quad;
    }
    public ThreadedMinimumPairwiseDistance(){

    }
    @Override
    public long minimumPairwiseDistance(int[] values) throws InterruptedException{
        Answer a = new Answer();
        Thread[] threads = new Thread[numThreads];
        int size = values.length / numThreads;

        for(int i=0; i<numThreads; i++){
            ThreadedMinimumPairwiseDistance pd = new ThreadedMinimumPairwiseDistance(values, a, i);
            threads[i] = new Thread(pd);
            threads[i].start();
        }

        for(int i=0; i<numThreads; i++){
            threads[i].join();
        }
        return a.getAnswer();
    }


    public void run(){
        switch(quadrant + 1){
            case 1: quad1(); break;
            case 2: quad2(); break;
            case 3: quad3(); break;
            case 4: quad4(); break;
        }
        while(!answer.getLock()){

        }
        if(answer.getAnswer() > bestSoFar){
            answer.setAnswer(bestSoFar);
        }
        answer.dropLock();
    }
    private void quad1() {
        for (int i = 0; i < values.length / 2; i++) {
            int vi = values[i];
            for (int j = 0; j < values.length && j < i; j++) {
                int v = Math.abs(vi - values[j]);
                if(v < bestSoFar){
                    bestSoFar = v;
                }
            }
        }
    }
    private void quad2() {
        for (int i = values.length / 2; i < values.length; i++) {
            int vi = values[i];
            for (int j = 0; j < values.length / 2 && j < (i - values.length / 2); j++) {
                int v = Math.abs(vi - values[j]);
                if(v < bestSoFar){
                    bestSoFar = v;
                }
            }
        }
    }
    private void quad3() {
        for (int i = values.length / 2; i < values.length; i++) {
            int vi = values[i];
            for (int j = (i - values.length / 2); j < values.length / 2; j++) {
                int v = Math.abs(vi - values[j]);
                if(v < bestSoFar){
                    bestSoFar = v;
                }
            }
        }
    }
    private void quad4() {
        for (int i = values.length/2; i<values.length;i++) {
            int vi = values[i];
            for (int j = values.length /2; j < i; j++) {
                int v = Math.abs(vi - values[j]);
                if(v < bestSoFar){
                    bestSoFar = v;
                }
            }
        }
    }
    static class Answer{
        private int answer = Integer.MAX_VALUE;

        private boolean lock = false;

        synchronized boolean getLock(){
            if(lock){
                return false;
            }else{
                lock = true;
                return true;
            }
        }

        synchronized void dropLock(){
            lock = false;
        }


        synchronized int getAnswer(){
            return answer;
        }

        synchronized void setAnswer(int newAnswer){
            answer = newAnswer;
        }
    }
}
