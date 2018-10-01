package com.mygdx.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.mygdx.game.Assets;
/**
 * @author Jeff
 * Abstract class to define shared characteristics of Screens
 *
 */
public abstract class AbstractGameScreen implements Screen {
	 protected Game game;
	 public AbstractGameScreen (Game game) 
	 {
		 this.game = game;
	 }
	 public abstract void render (float deltaTime);
	 public abstract void resize (int width, int height);
	 public abstract void show ();
	 public abstract void hide ();
	 public abstract void pause ();
	 
	 public void resume () {
		 Assets.instance.init(new AssetManager());
	 }
	 public void dispose () {
		 Assets.instance.dispose();
	 }
	}