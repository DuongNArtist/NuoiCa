package com.tvi.nuoica.entities;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.tvi.nuoica.GameActivity;
import com.tvi.nuoica.R;
import com.tvi.nuoica.engine.GameBitmap;
import com.tvi.nuoica.engine.GameSound;
import com.tvi.nuoica.engine.GameThread;
import com.tvi.nuoica.engine.GameView;

public class Tank implements Action {

    public static final int NUMBER = 6;
    public static final int MONEY = 200;
    public static final int WIDTH = 800;
    public static final int HEIGHT = 480;
    public static final int EGG_PRICE = 1000;
    public static final int TIME_TO_BATTLE = 60 * GameThread.FPS;

    public ArrayList<Enemy> enemies;
    public ArrayList<Ally> allies;
    public ArrayList<Fish> fishes;
    public ArrayList<Food> foods;
    public ArrayList<Coin> coins;
    public ArrayList<Shell> shells;
    public ArrayList<Bullet> bullets;
    public Random random;
    public int money;
    public int levelOfTank;
    public int levelOfFood;
    public int levelOfEggs;
    public int levelOfGunz;
    public int levelOfEnemy;
    public int timeToBattle;
    public int numberOfEnemies;
    public boolean buyingFood;
    public boolean buyingFish;
    public boolean buyingBullet;
    public boolean fighting;
    public boolean playing;
    public float x;
    public float y;

    public Tank(int level) {
        levelOfTank = level % NUMBER;
        levelOfEggs = level;
        numberOfEnemies = level / Enemy.NUMBER + 1;
        levelOfFood = 0;
        levelOfEnemy = 0;
        levelOfGunz = 0;
        money = MONEY;
        timeToBattle = TIME_TO_BATTLE;
        buyingFood = false;
        buyingBullet = false;
        buyingFish = false;
        fighting = false;
        playing = true;
        random = new Random();
        enemies = new ArrayList<Enemy>();
        foods = new ArrayList<Food>();
        coins = new ArrayList<Coin>();
        shells = new ArrayList<Shell>();
        bullets = new ArrayList<Bullet>();
        allies = new ArrayList<Ally>();
        fishes = new ArrayList<Fish>();
        if (level > 0) {
            for (int i = 0; i < level; i++) {
                allies.add(new Ally(i % Ally.NUMBER));
            }
        }
        for (int i = 0; i < 2; i++) {
            fishes.add(new Fish(0));
        }
    }

    public boolean buyFish() {
        if (playing && money >= Fish.PRICE && fishes.size() < Fish.LIMIT) {
            money -= Fish.PRICE;
            buyingFish = true;
            GameActivity.mGameSound.playSound(GameSound.fish_new);
        } else {
            GameActivity.mGameSound.playSound(GameSound.buy_no);
            // GameActivity.showGetCoinDialog();
        }
        return buyingFish;
    }

    public boolean buyEggs() {
        if (playing) {
            int price = 2 * (levelOfEggs + 1) * EGG_PRICE;
            if (money >= price) {
                money -= price;
                GameActivity.mGameSound.playSound(GameSound.buy_yes);
                return true;
            } else {
                // GameActivity.showGetCoinDialog();
                GameActivity.mGameSound.playSound(GameSound.buy_no);
            }
        }
        return false;
    }

    public boolean buyFood() {
        if (playing) {
            int price = Food.PRICES[levelOfFood];
            if (levelOfFood < Food.PRICES.length - 1 & money >= price
                    && foods.size() < Food.LIMIT) {
                money -= price;
                levelOfFood++;
                GameActivity.mGameSound.playSound(GameSound.buy_yes);
                return true;
            } else {
                // GameActivity.showGetCoinDialog();
                GameActivity.mGameSound.playSound(GameSound.buy_no);
            }
        }
        return false;
    }

    public boolean buyGunz() {
        if (playing) {
            int price = Bullet.PRICES[levelOfGunz];
            if (levelOfGunz < Bullet.PRICES.length - 1 & money >= price) {
                money -= price;
                levelOfGunz++;
                GameActivity.mGameSound.playSound(GameSound.buy_yes);
                return true;
            } else {
                GameActivity.mGameSound.playSound(GameSound.buy_no);
                // GameActivity.showGetCoinDialog();
            }
        }
        return false;
    }

    @Override
    public void update() {
        if (playing) {
            if (!fighting) {
                timeToBattle--;
                if (timeToBattle == 9 * GameThread.FPS) {
                    GameActivity.mGameSound.playMusic(R.raw.danger);
                }
                if (timeToBattle <= 0) {
                    fighting = true;
                    for (int index = 0; index < numberOfEnemies; index++) {
                        enemies.add(new Enemy(levelOfEnemy));
                    }
                    GameActivity.mGameSound.playMusic(R.raw.battle);
                }
            }
            for (int index = 0; index < enemies.size(); index++) {
                Enemy enemy = enemies.get(index);
                enemy.update();
                if (!enemy.alive) {
                    enemies.remove(index);
                    coins.add(new Coin(Coin.VALUES.length - 1, enemy.x, enemy.y));
                    timeToBattle = TIME_TO_BATTLE;
                    fighting = false;
                    levelOfEnemy++;
                    if (GameActivity.mGameSound.mMusic) {
                        GameActivity.mGameSound.mMediaPlayer.pause();
                    }
                    GameActivity.mGameSound.playMusic(R.raw.tank_0
                            + levelOfTank);
                    GameActivity.mGameSound.playSound(GameSound.enemy_die);
                }
            }
            for (Ally ally : allies) {
                ally.update();
                ally.collectProduct(coins, shells);
                if (ally.making) {
                    ally.making = false;
                    foods.add(new Food(ally.level, ally.x - ally.width / 2,
                            ally.y));
                    shells.add(new Shell(ally.level, ally.x + ally.width / 2,
                            ally.y));
                }
            }
            if (buyingFish) {
                buyingFish = false;
                fishes.add(new Fish(0));
            }
            for (int index = 0; index < fishes.size(); index++) {
                Fish fish = fishes.get(index);
                fish.findFood(foods);
                fish.attackedBy(enemies);
                fish.update();
                if (fish.making) {
                    fish.making = false;
                    coins.add(new Coin(fish.level, fish.x, fish.y));
                }
                if (!fish.alive) {
                    fishes.remove(index);
                    if (fishes.size() == 0) {
                        GameActivity.startResultActivity(false);
                    }
                }
            }
            if (buyingFood) {
                buyingFood = false;
                foods.add(new Food(levelOfFood, x, y));
                GameActivity.mGameSound.playSound(GameSound.food);
            }
            for (int index = 0; index < foods.size(); index++) {
                Food food = foods.get(index);
                food.update();
                if (!food.alive) {
                    foods.remove(index);
                }
            }
            for (int index = 0; index < coins.size(); index++) {
                Coin coin = coins.get(index);
                coin.update();
                if (!coin.alive) {
                    if (money + coin.value < Integer.MAX_VALUE) {
                        money += coin.value;
                    }
                    coins.remove(index);
                }
            }
            for (int index = 0; index < shells.size(); index++) {
                Shell shell = shells.get(index);
                shell.update();
                if (!shell.alive) {
                    if (money + shell.value < Integer.MAX_VALUE) {
                        money += shell.value;
                    }
                    shells.remove(index);
                }
            }
            if (buyingBullet) {
                buyingBullet = false;
                bullets.add(new Bullet(levelOfGunz, x, y));
                GameActivity.mGameSound.playSound(GameSound.gun_00
                        + levelOfGunz);
            }
            for (int index = 0; index < bullets.size(); index++) {
                Bullet bullet = bullets.get(index);
                bullet.update();
                if (!bullet.alive) {
                    bullets.remove(index);
                }
            }
            GameActivity.updateText(money);
        }
    }

    @Override
    public boolean touch(MotionEvent event) {
        if (playing) {
            if (enemies.size() == 0) {
                boolean touched = false;
                if (!touched) {
                    for (Coin coin : coins) {
                        touched = coin.touch(event);
                        if (touched) {
                            break;
                        }
                    }
                }
                if (!touched) {
                    for (Shell shell : shells) {
                        touched = shell.touch(event);
                        if (touched) {
                            break;
                        }
                    }
                }
                if (!touched) {
                    int price = Food.COSTS[levelOfFood];
                    if (money >= price) {
                        money -= price;
                        buyingFood = true;
                        x = event.getX() * Tank.WIDTH / GameView.width;
                        y = event.getY() * Tank.HEIGHT / GameView.height;
                    } else {
                        GameActivity.mGameSound.playSound(GameSound.buy_no);
                        // GameActivity.showGetCoinDialog();
                    }
                }
            } else {
                int price = Bullet.COSTS[levelOfGunz];
                if (money >= price) {
                    for (Enemy enemy : enemies) {
                        if (enemy.touch(event)) {
                            x = event.getX() * Tank.WIDTH / GameView.width;
                            y = event.getY() * Tank.HEIGHT / GameView.height;
                            money -= price;
                            buyingBullet = true;
                            enemy.attackedBy(Bullet.DAMAGES[levelOfGunz]);
                            break;
                        }
                    }
                } else {
                    GameActivity.mGameSound.playSound(GameSound.buy_no);
                    // GameActivity.showGetCoinDialog();
                }
            }
        }
        return true;
    }

    @Override
    public void render(Canvas canvas) {
        canvas.drawBitmap(GameActivity.mGameBitmap.get(GameBitmap.TANKS
                + levelOfTank % 6), 0, 0, null);
        for (Food food : foods) {
            food.render(canvas);
        }
        for (Fish fish : fishes) {
            fish.render(canvas);
        }
        for (Ally ally : allies) {
            ally.render(canvas);
        }
        for (Enemy enemy : enemies) {
            enemy.render(canvas);
        }
        for (Coin coin : coins) {
            coin.render(canvas);
        }
        for (Shell shell : shells) {
            shell.render(canvas);
        }
        for (Bullet bullet : bullets) {
            bullet.render(canvas);
        }
    }

}
