package robson;
import robocode.*;
import robocode.util.Utils;
import java.awt.Color;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * ROBSON GODOFREDO EUGÊNIO - walls defensivo / tracker ofensivo bot por:
 * 												   INGRIDY, SANT e SOFIA.
 *
 * ROBSON = filho da glória ilustre (germanico).
 * GODOFREDO = deus da paz (germanico).
 * EUGÊNIO = nobre (grego/latino).
 **/
public class godofredo extends AdvancedRobot {

	boolean ofensiva = false; // define estratégia de operação: defensiva / ofensiva.

	// declaração de independência *DEFENSIVA*
	boolean check; // controle da movimentação, usado para que o robô
	// não vire caso haja um oponente no seu caminho.
	double move;   // define quanto o robô se move.

	// declaração de independência *OFENSIVA*
	double enemyX;
	double enemyY;
	double enemyHeading;
	double enemyVelocity;

	/**
	 * RUN: comportamento padrão do GODOFREDO.
	 */
	public void run() {

		while (true) { // abre laço de repetição RUN.

			// TESTANDO quantos adversários ainda estão na arena.
			if (getOthers() == 1) {
				System.out.println("LAST STANDING TRIBUTE: ofensiva!");
				ofensiva = true;
			} else {
				System.out.println("DEFENSIVA!");
			} // fecha laço condicional (getOthers == 1).

			/**
			 *    DEFENSIVA
			 */
			if (!ofensiva) { // WALLS!

				// LIMITA o movimento do robo de acordo com o tamanho da arena.
				move = Math.max(getBattleFieldWidth(), getBattleFieldHeight());

				// inicializa o check em falso.
				check = false;

				// vira para a parede direita e aponta o robô para cima. 
				turnLeft(getHeading() % 90);
				// se move na medida delimitada pelo tamanho da arena
				ahead(move);

				// gira a arma para ESQUERDA, depois o GODOFREDO.
				check = true;
				turnGunRight(90);
				turnRight(90);

				while (true) { // laço de repetição WALLS.
					check = true;
					ahead(move);
					check = false;
					turnRight(90);

					// TESTANDO quantos adversários ainda estão na arena.
					if (getOthers() == 1) {
						System.out.println("LAST STANDING TRIBUTE: ofensiva!");
						ofensiva = true;
						break;
					} // fecha laço condicional (getOthers == 1).

				} // fecha laço de repetição WALLS.

			} // fecha DEFENSIVA


			/**
			 *    OFENSIVA
			 */
			if (ofensiva) { // TRACKER!

				// Set radar and gun to turn independently of the robot's body
				setAdjustRadarForRobotTurn(true);
				setAdjustGunForRobotTurn(true);

				// Robot main loop
				while (true) {
					// Keep turning the radar to scan for robots
					setTurnRadarRight(360);
					execute();
				}

			} // fecha OFENCIVA TRACKER.

		} // fecha laço de repetição RUN.

	} // fecha método RUN.


	/**
	 * onScannedRobot: comportamento do GODOFREDO ao scannear um bot.
	 */
	public void onScannedRobot(ScannedRobotEvent e) {

		double bulletPower = 2;
		System.out.println("Bullet Power: " + bulletPower);


		// Determina a força do tiro baseando-se na energia disponível
		if (getEnergy() < 20) {
			bulletPower = 2;

			// Retorna o valor da energia empregada no tiro
			System.out.println("Bullet Power: " + bulletPower);

			if (20 > getEnergy()) {
				bulletPower = 1;

				// Retorna o valor da energia empregada no tiro
				System.out.println("Bullet Power: " + bulletPower);

			}
		}

		/**
		 *    DEFENSIVA : COUNTER CLOCKWISE WALLS!
		 */

		if (!ofensiva) { // WALLS!

			fire(bulletPower);
			// caso exista um bot na próxima parede, impede o
			// movimendo do GODOFREDO até que esse bot se mova.
			if (check) {
				scan();
			}

		} // fecha DEFENSIVA WALLS.


		/**
		 *    OFENSIVA: ADVENCED TRACKER BOT!
		 */
		if (ofensiva) { // TRACKER!

			// Calculate enemy's position
			double enemyBearing = getHeading() + e.getBearing();
			enemyX = getX() + e.getDistance() * Math.sin(Math.toRadians(enemyBearing));
			enemyY = getY() + e.getDistance() * Math.cos(Math.toRadians(enemyBearing));
			enemyHeading = e.getHeading();
			enemyVelocity = e.getVelocity();

			// Predict the future position of the enemy
			double bulletSpeed = 20 - 3 * bulletPower;
			double predictedX = enemyX;
			double predictedY = enemyY;
			double deltaTime = 0;

			while ((++deltaTime) * bulletSpeed < Math.hypot(predictedX - getX(), predictedY - getY())) {
				predictedX += Math.sin(Math.toRadians(enemyHeading)) * enemyVelocity;
				predictedY += Math.cos(Math.toRadians(enemyHeading)) * enemyVelocity;

				// Ensure prediction stays within battlefield boundaries
				predictedX = Math.max(Math.min(predictedX, getBattleFieldWidth() - 18), 18);
				predictedY = Math.max(Math.min(predictedY, getBattleFieldHeight() - 18), 18);
			}

			// Aim the gun at the predicted future position of the enemy
			double gunTurn = Utils.normalRelativeAngleDegrees(Math.toDegrees(Math.atan2(predictedX - getX(), predictedY - getY())) - getGunHeading());
			setTurnGunRight(gunTurn);
			setFire(bulletPower);

			// Track the enemy
			setTurnRight(e.getBearing());
			setAhead(e.getDistance() - 140);  // Maintain a distance of about 140 units

			// Adjust radar to lock on the enemy
			double radarTurn = Utils.normalRelativeAngleDegrees(enemyBearing - getRadarHeading());
			setTurnRadarRight(radarTurn);

		} // fecha OFENSIVA TRACKER.

	}// fecha método onScannedRobot.



	/**
	 * onHitRobot:  Set him as our new target
	 */
	public void onHitRobot(HitRobotEvent e) {

		/**
		 *    DEFENSIVA : COUNTER CLOCKWISE WALLS!
		 */
		if (!ofensiva) { // WALLS!

			if (e.getBearing() > -90 && e.getBearing() < 90) {
				// se o bot estiver na frente do GODOFREDO:
				back(100);
			}
			else {
				// se não...
				ahead(100);
			}

		} // fecha DEFENSIVA WALLS.

	} // fecha método onHitRobot.



	/**
	 * onHitByBullet: comportamento do GODOFREDO ao ser atingido por uma bala.
	 */
	public void onHitByBullet(HitByBulletEvent e) {

		/**
		 *    OFENSIVA: ADVENCED TRACKER BOT!
		 */
		if (ofensiva) { // TRACKER!

			// Move perpendicularly to the bullet's direction
			setTurnRight(normalizeBearing(e.getBearing() + 90));
			setAhead(100);

		} // fecha OFENSIVA TRACKER.

	} // fecha método onHitByBullet.



	/**
	 * onHitWall: comportamento do GODOFREDO ao bater contra a parede.
	 */
	public void onHitWall(HitWallEvent e) {

		/**
		 *    OFENSIVA: ADVENCED TRACKER BOT!
		 */
		if (ofensiva) { // TRACKER!

			// Move away from the wall
			setTurnRight(normalizeBearing(180 - getHeading()));
			setAhead(100);

		} // fecha OFENSIVA TRACKER.

	} // fecha método onHitWall.



	/**
	 * onWin: GODOFREDO dança ao vencer.
	 */
	public void onWin(WinEvent e) {

		for (int i = 0; i < 200; i++) {
			turnRight(40);
			turnLeft(40);
		}

	} // fecha método onWin.

	/**
	 * Helper method to normalize a bearing to between +180 and -180
	 */
	double normalizeBearing(double angle) {
		while (angle > 180) angle -= 360;
		while (angle < -180) angle += 360;
		return angle;
	}

} // fecha classe SUPERGODOFREDO
