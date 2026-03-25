package INF1009_P3_02;

import INF1009_P3_02.Collision.CollisionManager;
import INF1009_P3_02.Entity.EntityManager;
import INF1009_P3_02.InputOutput.InputOutputManager;
import INF1009_P3_02.Movement.MovementManager;
import INF1009_P3_02.Observer.GameEventManager;
import INF1009_P3_02.Scene.SceneManager;
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