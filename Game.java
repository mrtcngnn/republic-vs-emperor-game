import javax.swing.JFrame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

public class Game extends JFrame implements KeyListener, MouseListener {

    Random random;
    private static AirCraft aircraft;
    private ArrayList<Enemy> enemies;
    private ArrayList<Friend> friends;
    private ArrayList<GunAircraft> aircraftFires;
    private ArrayList<GunEnemy> enemyFires;
    private ArrayList<GunFriend> friendFires;

    public Game() {
        Game.aircraft = new AirCraft();
        this.enemies = new ArrayList<>();
        this.friends = new ArrayList<>();
        this.aircraftFires = new ArrayList<>();
        this.enemyFires = new ArrayList<>();
        this.friendFires = new ArrayList<>();
        this.random = new Random();
        setTitle("Republic vs Emperor");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 500);
        addKeyListener(this);
        addMouseListener(this);
        setVisible(true);
    }

    public class AirCraft extends Thread {
        public int x;
        public int y;
        public long fire;

        public AirCraft() {
            this.x = 250;
            this.y = 250;
            this.fire = System.nanoTime();
            Game.aircraft = this;
            repaint();
        }
    }

    public class Enemy extends Thread {
        public int x;
        public int y;
        public String position;
        public Random randomEnemy;
        boolean alive = true;
        public long move;
        public long fire;

        public Enemy() {
            this.randomEnemy = new Random();
            this.fire = System.nanoTime();
            this.x = random.nextInt(49) * 10;
            this.y = random.nextInt(47) * 10 + 30;
            this.move = System.nanoTime();
            this.fire = System.nanoTime();
            while (true) {
                boolean enemiesContains = false;
                boolean friendsContains = false;
                for (int i = 0; i < enemies.size(); i++) {
                    if (this.x == enemies.get(i).x && this.y == enemies.get(i).y) {
                        enemiesContains = true;
                        break;
                    }
                }
                for (int i = 0; i < friends.size(); i++) {
                    if (this.x == friends.get(i).x && this.y == friends.get(i).y) {
                        friendsContains = true;
                        break;
                    }
                }
                if ((this.x == 250 && this.y == 250) || enemiesContains || friendsContains) {
                    this.x = random.nextInt(49) * 10;
                    this.y = random.nextInt(47) * 10 + 30;
                } else {
                    break;
                }
            }
            enemies.add(this);
            repaint();
        }

        public void run() {
            while (this.alive) {
                double moveCheck = (System.nanoTime() - this.move) / 1e6;
                if (moveCheck >= 500) {
                    int direction = randomEnemy.nextInt(4);
                    switch (direction) {
                        case 0:
                            if (this.x - 10 >= 0) {
                                this.move(this.x - 10, this.y);
                            }
                            break;
                        case 1:
                            if (this.y - 10 >= 30) {
                                this.move(this.x, this.y - 10);
                            }
                            break;
                        case 2:
                            if (this.x + 10 <= 490) {
                                this.move(this.x + 10, this.y);
                            }
                            break;
                        case 3:
                            if (this.y + 10 <= 490) {
                                this.move(this.x, this.y + 10);
                            }
                            break;
                        default:
                            break;
                    }
                }
                double fireCheck = (System.nanoTime() - this.fire) / 1e6;
                if (fireCheck >= 1000) {
                    this.fire();
                }
            }
        }

        public void move(int x, int y) {
            if (x == Game.aircraft.x && y == Game.aircraft.y) {
                killAllThreads();
                new GameOverPopUp("You Lost");
            }
            boolean enemiesContains = false;
            for (int i = 0; i < enemies.size(); i++) {
                if (x == enemies.get(i).x && y == enemies.get(i).y) {
                    enemiesContains = true;
                    break;
                }
            }
            if (!enemiesContains) {
                for (int i = 0; i < friends.size(); i++) { // collusion with friend
                    if (x == friends.get(i).x && y == friends.get(i).y) { // stop both thread
                        this.alive = false;
                        enemies.remove(this);
                        friends.get(i).alive = false;
                        friends.remove(i);
                        break;
                    }
                }
                if (this.alive) { // collusion with friend fire
                    for (int i = 0; i < friendFires.size(); i++) { // stop this thread
                        if (x == friendFires.get(i).x && y == friendFires.get(i).y) {
                            this.alive = false;
                            enemies.remove(this);
                            break;
                        }
                    }
                    if (this.alive) { // collusion with aircraft fire
                        for (int i = 0; i < aircraftFires.size(); i++) { // stop this thread
                            if (x == aircraftFires.get(i).x && y == aircraftFires.get(i).y) {
                                this.alive = false;
                                enemies.remove(this);
                                break;
                            }
                        }
                    }
                    if (this.alive) {
                        this.x = x;
                        this.y = y;
                        this.move = System.nanoTime();
                    }
                }
            }
        }

        public void fire() {
            if (this.x - 10 >= 0) {
                GunEnemy gunEnemy = new GunEnemy(this.x - 10, this.y, 0);
                gunEnemy.start();
                enemyFires.add(gunEnemy);
                this.fire = System.nanoTime();
            }
            if (this.x + 10 <= 490) {
                GunEnemy gunEnemy = new GunEnemy(this.x + 10, this.y, 1);
                gunEnemy.start();
                enemyFires.add(gunEnemy);
                this.fire = System.nanoTime();
            }
        }
    }

    public class Friend extends Thread {
        public int x;
        public int y;
        public String position;
        public Random randomFriend;
        public boolean alive = true;
        public long move;
        public long fire;

        public Friend() {
            this.randomFriend = new Random();
            this.x = random.nextInt(49) * 10;
            this.y = random.nextInt(47) * 10 + 30;
            this.move = System.nanoTime();
            this.fire = System.nanoTime();
            while (true) {
                boolean enemiesContains = false;
                boolean friendsContains = false;
                for (int i = 0; i < enemies.size(); i++) {
                    if (this.x == enemies.get(i).x && this.y == enemies.get(i).y) {
                        enemiesContains = true;
                        break;
                    }
                }
                for (int i = 0; i < friends.size(); i++) {
                    if (this.x == friends.get(i).x && this.y == friends.get(i).y) {
                        friendsContains = true;
                        break;
                    }
                }
                if ((this.x == 250 && this.y == 250) || enemiesContains || friendsContains) {
                    this.x = random.nextInt(49) * 10;
                    this.y = random.nextInt(47) * 10 + 30;
                } else {
                    break;
                }
            }
            friends.add(this);
            repaint();
        }

        public void run() {
            while (this.alive) {
                double moveCheck = (System.nanoTime() - this.move) / 1e6;
                if (moveCheck >= 500) {
                    int direction = randomFriend.nextInt(4);
                    switch (direction) {
                        case 0:
                            if (this.x - 10 >= 0) {
                                this.move(this.x - 10, this.y);
                            }
                            break;
                        case 1:
                            if (this.y - 10 >= 30) {
                                this.move(this.x, this.y - 10);
                            }
                            break;
                        case 2:
                            if (this.x + 10 <= 490) {
                                this.move(this.x + 10, this.y);
                            }
                            break;
                        case 3:
                            if (this.y + 10 <= 490) {
                                this.move(this.x, this.y + 10);
                            }
                            break;
                        default:
                            break;
                    }
                }
                double fireCheck = (System.nanoTime() - this.fire) / 1e6;
                if (fireCheck >= 1000) {
                    this.fire();
                }
            }
        }

        public void move(int x, int y) {
            if (x != Game.aircraft.x && y != Game.aircraft.y) {
                boolean friendsContains = false;
                for (int i = 0; i < friends.size(); i++) {
                    if (x == friends.get(i).x && y == friends.get(i).y) {
                        friendsContains = true;
                        break;
                    }
                }
                if (!friendsContains) {
                    for (int i = 0; i < enemies.size(); i++) { // collusion with enemy
                        if (x == enemies.get(i).x && y == enemies.get(i).y) { // stop both thread
                            this.alive = false;
                            friends.remove(this);
                            enemies.get(i).alive = false;
                            enemies.remove(enemies.get(i));
                            break;
                        }
                    }
                    if (this.alive) { // collusion with enemy fire
                        for (int i = 0; i < enemyFires.size(); i++) { // stop this thread
                            if (x == enemyFires.get(i).x && y == enemyFires.get(i).y) {
                                this.alive = false;
                                friends.remove(this);
                                break;
                            }
                        }
                        if (this.alive) {
                            this.x = x;
                            this.y = y;
                            this.move = System.nanoTime();
                        }
                    }
                }
            }
        }

        public void fire() {
            if (this.x - 10 >= 0) {
                GunFriend gunFriend = new GunFriend(this.x - 10, this.y, 0);
                gunFriend.start();
                friendFires.add(gunFriend);
                this.fire = System.nanoTime();
            }
            if (this.x + 10 <= 490) {
                GunFriend gunFriend = new GunFriend(this.x + 10, this.y, 1);
                gunFriend.start();
                friendFires.add(gunFriend);
                this.fire = System.nanoTime();
            }
        }
    }

    public class GunAircraft extends Thread {
        public int x;
        public int y;
        public int direction;
        public long move;
        public boolean alive = true;

        public GunAircraft(int x, int y, int direction) {
            this.x = x;
            this.y = y;
            this.direction = direction;
            this.move = System.nanoTime();
        }

        public void run() {
            while (this.alive) {
                double moveCheck = (System.nanoTime() - this.move) / 1e6;
                if (moveCheck >= 100) {
                    if (this.direction == 0) {
                        if (this.x - 10 >= 0) {
                            this.x -= 10;
                            repaint();
                            for (int i = 0; i < enemies.size(); i++) {
                                if (this.x == enemies.get(i).x && this.y == enemies.get(i).y) {
                                    enemies.get(i).alive = false;
                                    enemies.remove(i);
                                    this.alive = false;
                                    aircraftFires.remove(this);
                                    break;
                                }
                            }
                            checkGameOver("You Win :)");
                            this.move = System.nanoTime();
                        } else {
                            this.alive = false;
                            aircraftFires.remove(this);
                        }
                    } else if (this.direction == 1) {
                        if (this.x + 10 <= 490) {
                            this.x += 10;
                            repaint();
                            for (int i = 0; i < enemies.size(); i++) {
                                if (this.x == enemies.get(i).x && this.y == enemies.get(i).y) {
                                    enemies.get(i).alive = false;
                                    enemies.remove(i);
                                    this.alive = false;
                                    aircraftFires.remove(this);
                                    break;
                                }
                            }
                            checkGameOver("You Win :)");
                            this.move = System.nanoTime();
                        } else {
                            this.alive = false;
                            aircraftFires.remove(this);
                        }
                    }
                }
            }
        }
    }

    public class GunEnemy extends Thread {
        public int x;
        public int y;
        public int direction;
        public long move;
        public boolean alive = true;

        public GunEnemy(int x, int y, int direction) {
            this.x = x;
            this.y = y;
            this.direction = direction;
            this.move = System.nanoTime();
        }

        public void run() {
            while (this.alive) {
                if (this.x == Game.aircraft.x && this.y == Game.aircraft.y) {
                    killAllThreads();
                    new GameOverPopUp("You Lost :/");
                }
                double moveCheck = (System.nanoTime() - this.move) / 1e6;
                if (moveCheck >= 100) {
                    if (this.direction == 0) {
                        if (this.x - 10 >= 0) {
                            this.x -= 10;
                            repaint();
                            for (int i = 0; i < friends.size(); i++) {
                                if (this.x == friends.get(i).x && this.y == friends.get(i).y) {
                                    friends.get(i).alive = false;
                                    friends.remove(i);
                                    this.alive = false;
                                    enemyFires.remove(this);
                                    break;
                                }
                            }
                            this.move = System.nanoTime();
                        } else {
                            this.alive = false;
                            enemyFires.remove(this);
                        }
                    } else if (this.direction == 1) {
                        if (this.x + 10 <= 490) {
                            this.x += 10;
                            repaint();
                            for (int i = 0; i < friends.size(); i++) {
                                if (this.x == friends.get(i).x && this.y == friends.get(i).y) {
                                    friends.get(i).alive = false;
                                    friends.remove(i);
                                    this.alive = false;
                                    enemyFires.remove(this);
                                    break;
                                }
                            }
                            this.move = System.nanoTime();
                        } else {
                            this.alive = false;
                            enemyFires.remove(this);
                        }
                    }
                }
            }
        }
    }

    public class GunFriend extends Thread {
        public int x;
        public int y;
        public int direction;
        public long move;
        public boolean alive = true;

        public GunFriend(int x, int y, int direction) {
            this.x = x;
            this.y = y;
            this.direction = direction;
            this.move = System.nanoTime();
        }

        public void run() {
            while (this.alive) {
                double moveCheck = (System.nanoTime() - this.move) / 1e6;
                if (moveCheck >= 100) {
                    if (this.direction == 0) {
                        if (this.x - 10 >= 0) {
                            this.x -= 10;
                            repaint();
                            for (int i = 0; i < enemies.size(); i++) {
                                if (this.x == enemies.get(i).x && this.y == enemies.get(i).y) {
                                    enemies.get(i).alive = false;
                                    enemies.remove(i);
                                    this.alive = false;
                                    friendFires.remove(this);
                                    break;
                                }
                            }
                            checkGameOver("You Win :)");
                            this.move = System.nanoTime();
                        } else {
                            this.alive = false;
                            friendFires.remove(this);
                        }
                    } else if (this.direction == 1) {
                        if (this.x + 10 <= 490) {
                            this.x += 10;
                            repaint();
                            for (int i = 0; i < enemies.size(); i++) {
                                if (this.x == enemies.get(i).x && this.y == enemies.get(i).y) {
                                    enemies.get(i).alive = false;
                                    enemies.remove(i);
                                    this.alive = false;
                                    friendFires.remove(this);
                                    break;
                                }
                            }
                            checkGameOver("You Win :)");
                            this.move = System.nanoTime();
                        } else {
                            this.alive = false;
                            friendFires.remove(this);
                        }
                    }
                }
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        int x = Game.aircraft.x;
        int y = Game.aircraft.y;
        if (e.getKeyCode() == 87) { // W
            y -= 10;
        } else if (e.getKeyCode() == 83) { // S
            y += 10;
        } else if (e.getKeyCode() == 65) { // A
            x -= 10;
        } else if (e.getKeyCode() == 68) { // D
            x += 10;
        }
        if (x <= 490 && y <= 490 && y >= 30) {
            boolean enemiesContains = false;
            boolean friendsContains = false;
            boolean enemyFiresContains = false;
            for (int i = 0; i < enemies.size(); i++) {
                if (x == enemies.get(i).x && y == enemies.get(i).y) {
                    enemiesContains = true;
                    break;
                }
            }
            for (int i = 0; i < friends.size(); i++) {
                if (x == friends.get(i).x && y == friends.get(i).y) {
                    friendsContains = true;
                    break;
                }
            }
            for (int i = 0; i < enemyFires.size(); i++) {
                if (x == enemyFires.get(i).x && y == enemyFires.get(i).y) {
                    enemyFiresContains = true;
                    break;
                }
            }
            if (enemiesContains || enemyFiresContains) {
                this.killAllThreads();
                new GameOverPopUp("You Lost :/");
            }
            if (!enemiesContains && !friendsContains && !enemyFiresContains) {
                Game.aircraft.x = x;
                Game.aircraft.y = y;
                repaint();
            }
        }
    }

    public void mousePressed(MouseEvent e) {
        double fireCheck = (System.nanoTime() - Game.aircraft.fire) / 1e6;
        if (fireCheck >= 1000) {
            if (Game.aircraft.x - 10 >= 0) {
                GunAircraft gunAircraft = new GunAircraft(Game.aircraft.x - 10, Game.aircraft.y, 0);
                gunAircraft.start();
                this.aircraftFires.add(gunAircraft);
            }
            if (Game.aircraft.x + 10 <= 490) {
                GunAircraft gunAircraft = new GunAircraft(Game.aircraft.x + 10, Game.aircraft.y, 1);
                gunAircraft.start();
                this.aircraftFires.add(gunAircraft);
            }
            Game.aircraft.fire = System.nanoTime();
        }
    }

    public void paint(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, 500, 500);
        g.setColor(Color.RED);
        g.fillRect(Game.aircraft.x, Game.aircraft.y, 10, 10);
        g.setColor(Color.BLACK);
        for (int i = 0; i < enemies.size(); i++) {
            g.fillRect(enemies.get(i).x, enemies.get(i).y, 10, 10);
        }
        g.setColor(Color.GREEN);
        for (int i = 0; i < friends.size(); i++) {
            g.fillRect(friends.get(i).x, friends.get(i).y, 10, 10);
        }
        g.setColor(Color.ORANGE);
        for (int i = 0; i < aircraftFires.size(); i++) {
            g.fillRect(aircraftFires.get(i).x, aircraftFires.get(i).y, 5, 5);
        }
        g.setColor(new Color(104, 52, 155));
        for (int i = 0; i < friendFires.size(); i++) {
            g.fillRect(friendFires.get(i).x, friendFires.get(i).y, 5, 5);
        }
        g.setColor(Color.BLUE);
        for (int i = 0; i < enemyFires.size(); i++) {
            g.fillRect(enemyFires.get(i).x, enemyFires.get(i).y, 5, 5);
        }
    }

    public void killAllThreads() {
        Game.aircraft.x = -1;
        Game.aircraft.y = -1;
        for (Enemy enemy : enemies) {
            enemy.alive = false;
        }
        for (Friend friend : friends) {
            friend.alive = false;
        }
        for (GunAircraft ga : aircraftFires) {
            ga.alive = false;
        }
        for (GunEnemy ge : enemyFires) {
            ge.alive = false;
        }
        for (GunFriend gf : friendFires) {
            gf.alive = false;
        }
    }

    public boolean checkGameOver(String message) {
        boolean gameOver = true;
        if (this.enemies.size() > 0) {
            gameOver = false;
        } else {
            killAllThreads();
            new GameOverPopUp(message);
        }
        return gameOver;
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

}
