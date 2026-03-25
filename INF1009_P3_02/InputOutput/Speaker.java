package INF1009_P3_02.InputOutput;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

//Manages audio volume settings and playback
public class Speaker {
    private float masterVolume;
    private float previousVolume = -1f; // Track previous volume to avoid duplicate logs
    private Music menuMusic;
    private Music gameMusic;
    private Music currentMusic;
    private Sound collisionSound;
    private Sound pickupSound;
    private Sound correctSound;
    private Sound wrongSound;
    private Sound gameEndSound;
    private boolean audioEnabled = true;
    private long CollisionCooldown = 0;

    public Speaker() {
        this.masterVolume = 0.5f;
    }

    public void loadContent() {
        try {
            // Check if audio files exist before trying to load them
            if (Gdx.files.internal("audio/music/game.mp3").exists()) {
                gameMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/music/game.mp3"));
                gameMusic.setLooping(true);
                gameMusic.setVolume(masterVolume);
            } else {
                System.out.println("WARNING: Game music file not found (audio/music/game.mp3). Music disabled.");
            }

            if (Gdx.files.internal("audio/music/menu.mp3").exists()) {
                menuMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/music/menu.mp3"));
                menuMusic.setLooping(true);
                menuMusic.setVolume(masterVolume);
            } else {
                System.out.println("WARNING: Menu music file not found (audio/music/menu.mp3). Music disabled.");
            }

            if (Gdx.files.internal("audio/Sounds/Collision.ogg").exists()) {
                collisionSound = Gdx.audio.newSound(Gdx.files.internal("audio/Sounds/collision.ogg"));
            } else {
                System.out.println("WARNING: Collision sound file not found (audio/Sounds/collision.ogg). Collision sounds disabled.");
            }

            if (Gdx.files.internal("audio/Sounds/pickup.mp3").exists()) {
                pickupSound = Gdx.audio.newSound(Gdx.files.internal("audio/Sounds/pickup.mp3"));
            } else {
                System.out.println("WARNING: Pickup sound file not found (audio/Sounds/pickup.mp3). Pickup sounds disabled.");
            }

            if (Gdx.files.internal("audio/Sounds/correct.mp3").exists()) {
                correctSound = Gdx.audio.newSound(Gdx.files.internal("audio/Sounds/correct.mp3"));
            } else {
                System.out.println("WARNING: Correct sound file not found (audio/Sounds/correct.mp3). Correct sounds disabled.");
            }

            if (Gdx.files.internal("audio/Sounds/wrong.mp3").exists()) {
                wrongSound = Gdx.audio.newSound(Gdx.files.internal("audio/Sounds/wrong.mp3"));
            } else {
                System.out.println("WARNING: Wrong sound file not found (audio/Sounds/wrong.mp3). Wrong sounds disabled.");
            }

            if (Gdx.files.internal("audio/Sounds/end.mp3").exists()) {
                gameEndSound = Gdx.audio.newSound(Gdx.files.internal("audio/Sounds/end.mp3"));
            } else {
                System.out.println("WARNING: Game end sound file not found (audio/Sounds/end.mp3). Game end sounds disabled.");
            }

            // Only disable audio if all files are missing
            if (gameMusic == null && menuMusic == null && collisionSound == null
                    && pickupSound == null && correctSound == null
                    && wrongSound == null && gameEndSound == null) {
                audioEnabled = false;
                System.out.println("WARNING: No audio files found. Audio is disabled.");
                System.out.println("To enable audio, add files to:");
                System.out.println("  - assets/audio/music/game.mp3");
                System.out.println("  - assets/audio/music/menu.mp3");
                System.out.println("  - assets/audio/Sounds/collision.ogg");
                System.out.println("  - assets/audio/Sounds/pickup.mp3");
                System.out.println("  - assets/audio/Sounds/correct.mp3");
                System.out.println("  - assets/audio/Sounds/wrong.mp3");
                System.out.println("  - assets/audio/Sounds/end.mp3");
            }
        } catch (Exception e) {
            audioEnabled = false;
            System.err.println("WARNING: Could not load audio files. Audio will be disabled.");
            e.printStackTrace();
        }
    }

    private void switchMusic(Music newTrack) {
        if (!audioEnabled || newTrack == null) return;
        if (currentMusic == newTrack) return; // Already playing this track

        if (currentMusic != null) {
            currentMusic.stop();
        }

        currentMusic = newTrack;
        currentMusic.setVolume(masterVolume);
        currentMusic.play();
    }

    public void playMenuMusic() {
        if (menuMusic != null) {
            switchMusic(menuMusic);
        } else if (gameMusic != null) {
            switchMusic(gameMusic);
        }
    }

    public void playGameMusic() {
        switchMusic(gameMusic);
    }

    public void stopMusic() {
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.stop();
        }
    }

    public void playBackgroundMusic() {
        playMenuMusic();
    }

    public void playCollisionSound() {
        long now = com.badlogic.gdx.utils.TimeUtils.millis();
        if (now - CollisionCooldown < 150) return; // 150ms cooldown
        CollisionCooldown = now;

        if (audioEnabled && collisionSound != null) {
            long id = collisionSound.play(1.0f);
            collisionSound.setPitch(id, 1.2f);
        }
    }

    public void playPickupSound() {
        if (audioEnabled && pickupSound != null) {
            pickupSound.play(masterVolume);
        }
    }

    public void playCorrectSound() {
        if (audioEnabled && correctSound != null) {
            correctSound.play(masterVolume);
        }
    }

    public void playWrongSound() {
        if (audioEnabled && wrongSound != null) {
            float boostedVolume = Math.min(1.0f, masterVolume * 1.5f);
            wrongSound.play(boostedVolume);
        }
    }

    public void playGameEndSound() {
        if (audioEnabled && gameEndSound != null) {
            gameEndSound.play(masterVolume);
        }
    }

    public void playCountdownTick() {
        if (!audioEnabled) return;
        if (pickupSound != null) {
            long id = pickupSound.play(Math.min(1.0f, masterVolume * 0.9f));
            pickupSound.setPitch(id, 1.25f);
        } else if (correctSound != null) {
            long id = correctSound.play(Math.min(1.0f, masterVolume * 0.8f));
            correctSound.setPitch(id, 1.2f);
        }
    }

    public void playCountdownRecycle() {
        if (!audioEnabled) return;
        if (correctSound != null) {
            long id = correctSound.play(Math.min(1.0f, masterVolume));
            correctSound.setPitch(id, 1.05f);
        } else if (gameEndSound != null) {
            gameEndSound.play(Math.min(1.0f, masterVolume * 0.8f));
        }
    }

    public void playCountdownNumber(int number) {
        if (!audioEnabled) return;
        if (pickupSound != null) {
            float pitch = 1.05f + (number * 0.08f);
            long id = pickupSound.play(Math.min(1.0f, masterVolume * 0.95f));
            pickupSound.setPitch(id, pitch);
            return;
        }
        playCountdownTick();
    }

    public void playUiWhoosh() {
        if (!audioEnabled) return;
        if (collisionSound != null) {
            long id = collisionSound.play(Math.min(1.0f, masterVolume * 0.18f));
            collisionSound.setPitch(id, 1.6f);
        } else if (pickupSound != null) {
            long id = pickupSound.play(Math.min(1.0f, masterVolume * 0.35f));
            pickupSound.setPitch(id, 1.7f);
        }
    }

    public void setVolume(float volume) {
        float newVolume = Math.max(0, Math.min(1, volume));

        // Only log if the value actually changed (rounded to 2 decimals to avoid floating point noise)
        if (Math.abs(newVolume - previousVolume) > 0.01f) {
            previousVolume = newVolume;
            System.out.println("Volume changed to: " + String.format("%.2f", newVolume));
        }

        this.masterVolume = newVolume;
        if (audioEnabled && currentMusic != null) {
            currentMusic.setVolume(this.masterVolume);
        }
    }

    public float getVolume() {
        return masterVolume;
    }

    public boolean isAudioEnabled() {
        return audioEnabled;
    }

    public void dispose() {
        if (menuMusic != null) menuMusic.dispose();
        if (gameMusic != null) gameMusic.dispose();
        if (collisionSound != null) collisionSound.dispose();
        if (pickupSound != null) pickupSound.dispose();
        if (correctSound != null) correctSound.dispose();
        if (wrongSound != null) wrongSound.dispose();
        if (gameEndSound != null) gameEndSound.dispose();
    }
}