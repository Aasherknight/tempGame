package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.objects.AbstractGameObject;
import com.mygdx.game.objects.BunnyHead;
import com.mygdx.game.objects.Carrot;
import com.mygdx.game.objects.Clouds;
import com.mygdx.game.objects.Feather;
import com.mygdx.game.objects.Goal;
import com.mygdx.game.objects.GoldCoin;
import com.mygdx.game.objects.Mountains;
import com.mygdx.game.objects.Rock;
import com.mygdx.game.objects.WaterOverlay;

/**
 * Class for managing all objects contained within the game
 * level
 * @author Jeff
 */
public class Level 
{
	public static final String TAG = Level.class.getName();
	
	public enum BLOCK_TYPE
	{
		EMPTY(0,0,0), 					//Black
		ROCK(0,255,0), 					//Green
		PLAYER_SPAWNPOINT(255,255,255),	//White
		ITEM_FEATHER(255,0,255),		//Purple
		ITEM_GOLD_COIN(255,255,0),		//Yellow
		GOAL(255,0,0);					//Red - Aaron Gerber, pg 342
		
		private int color;
		
		private BLOCK_TYPE(int r, int g, int b)
		{
			color = r << 24 | g << 16 | b << 8 | 0xff;
		}
		
		public boolean sameColor(int color)	
		{
			return this.color == color;
		}
		
		public int getColor()
		{
			return color;
		}
	}
	
	//objects
	public Array<Rock> rocks;
	
	//decoration
	public Clouds clouds;
	public Mountains mountains;
	public WaterOverlay waterOverlay;
	
	//actors
	public BunnyHead bunnyHead;
	public Array<GoldCoin> goldcoins;
	public Array<Feather> feathers;
	
	//Aaron Gerber pg 342
	public Array<Carrot> carrots;
	public Goal goal;
	
	public Level (String filename)
	{
		init(filename);
	}
	
	public void update(float deltaTime)
	{
		bunnyHead.update(deltaTime);
		for(Rock rock: rocks)
			rock.update(deltaTime);
		for (GoldCoin goldCoin : goldcoins)
			goldCoin.update(deltaTime);
		for(Feather feather: feathers)
			feather.update(deltaTime);
		for (Carrot carrot : carrots)
			carrot.update(deltaTime);
		clouds.update(deltaTime);
	}
	
	private void init(String filename)
	{
		//player character
		bunnyHead = null;
		
		//objects
		rocks = new Array<Rock>();
		goldcoins = new Array<GoldCoin>();
		feathers = new Array<Feather>();
		carrots = new Array<Carrot>();
		
		//load image file that represents the level data
		Pixmap pixmap = new Pixmap(Gdx.files.internal(filename));
		
		//scan pixels form top-left to bottom-right
		int lastPixel = -1;
		
		for(int pixelY = 0; pixelY < pixmap.getHeight(); pixelY++)
		{
			for(int pixelX = 0; pixelX < pixmap.getWidth(); pixelX++)
			{
				//preparing a super so that the children can be used as if they were the super
				AbstractGameObject obj = null;
				float offsetHeight = 0;
				
				//height grows from bottom to top
				float baseHeight = pixmap.getHeight()-pixelY;
				
				//get color of current pixels as 32-bit RGBA value
				int currentPixel = pixmap.getPixel(pixelX, pixelY);
				
				//find matching color value to id the block type
				//create the listed object if there is a match
				
				//empty space
				if(BLOCK_TYPE.EMPTY.sameColor(currentPixel))
				{
					
				}
				//rock
				else if(BLOCK_TYPE.ROCK.sameColor(currentPixel))
				{
					//making the edge of a rock
					if(lastPixel != currentPixel)
					{
						obj = new Rock();
						float heightIncreaseFactor = 0.25f;
						offsetHeight = -2.5f;
						obj.position.set(pixelX, baseHeight * obj.dimension.y * heightIncreaseFactor + offsetHeight);
						rocks.add((Rock)obj);
					}
					else
					{
						rocks.get(rocks.size-1).increaseLength(1);
					}
				}
				//player spawn point
				else if(BLOCK_TYPE.PLAYER_SPAWNPOINT.sameColor(currentPixel))
				{
					obj = new BunnyHead();
					offsetHeight = -3.0f;
					obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
					bunnyHead = (BunnyHead)obj;
				}
				//feather
				else if(BLOCK_TYPE.ITEM_FEATHER.sameColor(currentPixel))
				{
					obj = new Feather();
					offsetHeight = -1.5f;
					obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
					feathers.add((Feather)obj);
				}
				//gold coin
				else if(BLOCK_TYPE.ITEM_GOLD_COIN.sameColor(currentPixel))
				{
					obj = new GoldCoin();
					offsetHeight = -1.5f;
					obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
					goldcoins.add((GoldCoin)obj);
				}
				//goal, Aaron Gerber pg 342
				else if(BLOCK_TYPE.GOAL.sameColor(currentPixel))
				{
					obj = new Goal();
					offsetHeight = -7.0f;
					obj.position.set(pixelX, baseHeight + offsetHeight);
					goal = (Goal)obj;
				}
				else
				{
					int r = 0xff & (currentPixel >>> 24); 	//Red color channel
					int g = 0xff & (currentPixel >>> 16);	//Green color channel
					int b = 0xff & (currentPixel >>> 8);	//Blue color channel
					int a = 0xff & currentPixel;			//alpha channel
					Gdx.app.error(TAG, "Unknown object at x<" + pixelX + "> y<" + pixelY + ">: r<" + r + "> g<" + g + "> b<" + b + "> a<" + a + ">");
				}
				lastPixel = currentPixel;
			}
		}
		
		//decoration
		clouds = new Clouds(pixmap.getWidth());
		clouds.position.set(0,2);
		mountains = new Mountains(pixmap.getWidth());
		mountains.position.set(-1,-1);
		waterOverlay = new WaterOverlay(pixmap.getWidth());
		waterOverlay.position.set(0,-3.6f); //changed y from -3.75 to -3.6 to raise water slightly
		
		//free memory
		pixmap.dispose();
		Gdx.app.debug(TAG, "Level '" + filename + "' loaded");
	}
	
	public void render(SpriteBatch batch)
	{		
		//Draw mountains
		mountains.render(batch);

		//Draw Goal
		goal.render(batch);
		
		//Draw rocks
		for(Rock rock : rocks)
			rock.render(batch);	
		
		//Draw Gold Coins
		for (GoldCoin goldCoin : goldcoins)
			goldCoin.render(batch);
		
		//Draw Feathers
		for(Feather feather: feathers)
			feather.render(batch);
		
		//Draw Carrots
		for (Carrot carrot : carrots)
			carrot.render(batch);
		
		//Draw Player Character
		bunnyHead.render(batch);
		
		//draw clouds
		clouds.render(batch);
		
		//draw Water overlay
		waterOverlay.render(batch);
	}
	
}
