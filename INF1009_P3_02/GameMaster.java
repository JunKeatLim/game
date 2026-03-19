package INF1009_P3_02;

import INF1009_P3_02.Collision.CollisionManager;
import INF1009_P3_02.Entity.EntityManager;
import INF1009_P3_02.InputOutput.InputOutputManager;
import INF1009_P3_02.Movement.MovementManager;
import INF1009_P3_02.Scene.SceneManager;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

public class GameMaster extends ApplicationAdapter {

    private SceneManager sceneManager;
    private InputOutputManager inputOutputManager;
    private EntityManager entityManager;
    private MovementManager movementManager;
    private CollisionManager collisionManager;

    @Override
    public void create() {
        sceneManager = new SceneManager();
        sceneManager.setGameMaster(this);

        entityManager = new EntityManager();
        movementManager = new MovementManager(entityManager);

        inputOutputManager = new InputOutputManager(sceneManager.getSettings(), movementManager);
        collisionManager = new CollisionManager(
            inputOutputManager.getSpeaker(),
            entityManager,
            sceneManager.getLogger()
        );

        sceneManager.setInputOutputManager(inputOutputManager);
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();

        // SceneManager forwards update/render to current scene
        sceneManager.update(dt);
        sceneManager.render();
    }

    @Override
    public void resize(int width, int height) {
        sceneManager.resize(width, height);
    }

    @Override
    public void dispose() {
        sceneManager.dispose();

        if (inputOutputManager != null) {
            inputOutputManager.dispose();
        }
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
