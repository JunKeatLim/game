package INF1009_P3_02;

import INF1009_P3_02.Engine.collision.CollisionManager;
import INF1009_P3_02.Engine.entity.EntityManager;
import INF1009_P3_02.Engine.io.InputOutputManager;
import INF1009_P3_02.Engine.movement.MovementManager;
import INF1009_P3_02.Engine.observer.GameEventManager;
import INF1009_P3_02.Game.scene.SceneManager;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

public class GameMaster extends ApplicationAdapter {

    private SceneManager sceneManager;
    private InputOutputManager inputOutputManager;
    private EntityManager entityManager;
    private MovementManager movementManager;
    private CollisionManager collisionManager;

    // Observer Subject — shared across the game
    private GameEventManager eventManager;

    @Override
    public void create() {
        sceneManager = new SceneManager();
        sceneManager.setGameMaster(this);

        entityManager = new EntityManager();
        movementManager = new MovementManager(entityManager);

        inputOutputManager = new InputOutputManager(sceneManager.getSettings(), movementManager);

        eventManager = new GameEventManager();

        collisionManager = new CollisionManager(
            inputOutputManager.getSpeaker(),
            entityManager,
            eventManager
        );

        sceneManager.setInputOutputManager(inputOutputManager);
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        sceneManager.update(dt);
        sceneManager.render();
    }

    @Override
    public void resize(int width, int height) {
        sceneManager.resize(width, height);
    }

    @Override
    public void dispose() {
        if (eventManager != null) eventManager.clearListeners();
        sceneManager.dispose();
        if (inputOutputManager != null) {
            inputOutputManager.dispose();
        }
    }

    public GameEventManager getEventManager() {
        return eventManager;
    }

    public InputOutputManager getInputOutputManager() {
        return inputOutputManager;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public MovementManager getMovementManager() {
        return movementManager;
    }

    public CollisionManager getCollisionManager() {
        return collisionManager;
    }
}