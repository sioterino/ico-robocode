package robson;
import robocode.*;
import java.awt.Color;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * Ofensivatracker - a robot by (your name here)
 */
public class godofredo extends Robot
{
	boolean ofensiva = false; // define estratégia de operação: defensiva / ofensiva


	// DECLARAÇÃO DE INDEPENDÊNCIA *OFENSIVA*
	int count = 0; // conta quanto tempo procura pelo adversário
	// durante a procura do adversário
	double gunTurnAmt; // quantidade de movimento da arma ao procurar o adversário
	String trackName; // nome do robo sendo mapeado

	public void run() {

		while (true) {
			
			System.out.print(getOthers());

			// caso sobre apenas um robo no campo
			if (getOthers() == 1) {
				System.out.println("LAST STANDING TRIBUTE");
				ofensiva = true;
			}

			/**
			 *    DEFENSIVA
			 */
			if (!ofensiva) { // walls (?)

				for (int i = 0; i <= 50; i++) {
					System.out.println("DEFENSIVA");
				}

			} // fecha DEFENSIVA

			/**
			 *    OFENSIVA
			 */
			if (ofensiva) { // tracker

				// preparo da arma
				trackName = null; // mapeamento nulo
				setAdjustGunForRobotTurn(true); // gira a arma/radar junto do tanque
				gunTurnAmt = 10; // arma gira 10 graus

				while (true) {
					// gira a arma/radar para procurar um robo
					turnGunRight(gunTurnAmt);

					count++; // quantas vezes tentou mapear

					// ESQUERDA caso 2 tentativas falhas de mapear adversário
					if (count > 2) {
						gunTurnAmt = -10;
					}

					// DIREITA caso 5 tentativas falhas de mapear adversário
					if (count > 5) {
						gunTurnAmt = 10;
					}

					// ENCONTRE OUTRO ADVERSÁRIO caso 10 tentativas falhas de mapear adversário
					if (count > 11) {
						trackName = null; // (onScannedRobot)
					}
				}

			} // fecha OFENCIVA

		} // fecha laço de repetição

	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {

		/**
		 *    DEFENSIVA
		 */
		if (!ofensiva) { // walls (?)

			for (int i = 0; i <= 50; i++) {
				System.out.println("DEFENSIVA");
			}

		} // fecha DEFENSIVA

		/**
		 *    OFENSIVA
		 */
		if (ofensiva) { // tracker

			// If we have a target, and this isn't it, return immediately
			// so we can get more ScannedRobotEvents.
			if (trackName != null && !e.getName().equals(trackName)) {
				return;
			}

			// define o adversário a ser mapeado
			if (trackName == null) {
				trackName = e.getName();
				out.println("Tracking " + trackName);
			}

			// adversário definido, contador ZERA
			count = 0;

			// APROXIME-SE caso o adversário esteja muito longe
			if (e.getDistance() > 150) {
				gunTurnAmt = (e.getBearing() + (getHeading() - getRadarHeading()));

				turnGunRight(gunTurnAmt);
				turnRight(e.getBearing());
				ahead(e.getDistance() - 140);
				return;
			}

			// adversário próximo
			gunTurnAmt = (e.getBearing() + (getHeading() - getRadarHeading()));
			turnGunRight(gunTurnAmt);
			fire(3);

			// AFASTE-SE caso o adversário esteja muito perto
			if (e.getDistance() < 100) {
				if (e.getBearing() > -90 && e.getBearing() <= 90) {
					back(40);
				} else {
					ahead(40);
				}
			}

			scan();

		} // fecha OFENSIVA

	}

	/**
	 * onHitRobot:  Set him as our new target
	 */
	public void onHitRobot(HitRobotEvent e) {

		/**
		 *    OFENSIVA
		 */
		if (ofensiva) {
			// imprime apenas se já não for o adversário sendo mapeado
			if (trackName != null && !trackName.equals(e.getName())) {
				out.println("MAPEANDO " + e.getName() + " POR CONTA DE COLISÃO");
			}

			// escolhe adversário a ser mapeado
			trackName = e.getName();

			// AFASTE-SE (radar inativo)
			gunTurnAmt = (e.getBearing() + (getHeading() - getRadarHeading()));
			turnGunRight(gunTurnAmt);
			fire(3);
			back(50);
		}

	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		back(10);
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		back(20);
	}

	/**
	 * onWin:  Do a victory dance
	 */
	public void onWin(WinEvent e) {
		for (int i = 0; i < 50; i++) {
			turnRight(30);
			turnLeft(30);
		}
	}
}