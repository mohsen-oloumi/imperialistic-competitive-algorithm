public class Nation implements Comparable<Nation>{
    public double x;
    public double y;
    public Long id;
    public Object metropolis;
    public double val;

    public Nation() {
    }

    public Nation(double x, double y, Long id, double val) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.val = val;
    }

    public Nation(double x, double y, Long id, Object metropolis, double val) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.metropolis = metropolis;
        this.val = val;
    }

    @Override
    public int compareTo(Nation o) {
        return (int) (o.val - this.val);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Object getMetropolis() {
        return metropolis;
    }

    public void setMetropolis(Object metropolis) {
        this.metropolis = metropolis;
    }

    public double getVal() {
        return val;
    }

    public void setVal(double val) {
        this.val = val;
    }
}
