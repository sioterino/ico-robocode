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
	double move;  // define quanto o robô se move.

	// declaração de independência *OFENSIVA*
	double enemyX;			// posição HORIZONTAL do adversário na arena.
	double enemyY;			// posição VERTICAL do adversário na arena.
	double enemyHeading;	// direção em que o adversário está olhando.
	double enemyVelocity;	// velocidade do adversário.

	/**
	 * RUN: comportamento padrão do GODOFREDO.
	 */
	public void run() {
	
		System.out.println(); // DEBUG.

		// colorindo o GODOFREDO (defensiva walss):
		setBodyColor(Color.cyan);		// CORPO: ciano.
		setGunColor(Color.lightGray);	// ARMA: cinza claro.
		setRadarColor(Color.pink);		// RADAR: rosa.
		setBulletColor(Color.white);	// BALA: branca.
		setScanColor(Color.blue);		// SCAN: azul.

		while (true) { // abre laço de repetição RUN.

			// TESTANDO quantos adversários ainda estão na arena.
			if (getOthers() == 1) {
				System.out.println("LAST STANDING TRIBUTE: ofensiva! \n");
				ofensiva = true;
			} else {
				System.out.println("DEFENSIVA! \n");
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
						System.out.println("LAST STANDING TRIBUTE: ofensiva! \n");
						ofensiva = true;
						break;
					} // fecha laço condicional (getOthers == 1).

				} // fecha laço de repetição WALLS.

			} // fecha DEFENSIVA


			/**
			 *    OFENSIVA
			 */
			if (ofensiva) { // TRACKER!
		
				// colorindo o GODOFREDO (ofensiva tracker):
				setBodyColor(Color.black);		// CORPOR: preto.
				setGunColor(Color.red);			// ARMA: vermelho.
				setRadarColor(Color.orange);	// RADAR: laranja.
				setBulletColor(Color.gray);		// BALA: cinza.
				setScanColor(Color.red);		// SCANNER: vermelho.



				// ajusta o radar e a arma para girarem independentemente do corpo.
				setAdjustRadarForRobotTurn(true);
				setAdjustGunForRobotTurn(true);

				while (true) { // laço de repetição TRACKER
				
					// permanece girando o radar a procura do último adversário.
					setTurnRadarRight(360);
					execute();
					
				} // fecha laço de repetição TRACKER

			} // fecha OFENCIVA TRACKER.

		} // fecha laço de repetição RUN.

	} // fecha método RUN.


	/**
	 * onScannedRobot: comportamento do GODOFREDO ao scannear um bot.
	 */
	public void onScannedRobot(ScannedRobotEvent e) {

	

		// definindo a força do tiro de acordo com a energia do GODOFREDO.
		double bulletPower = 2;
		
		if (20 > getEnergy()) {
			// CASO	energia < 20:
			bulletPower = 1;
		}
		
		// retorna o valor da energia empregada no tiro.
		System.out.println("ENERGIA DO GODOFREDO: " + getEnergy() + ".");
		System.out.println("FORÇA DO TIRO: " + bulletPower + ". \n");



		/**
		 *    DEFENSIVA : WALLS!
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

			// CALCULA posição do adversário.
			double enemyBearing = getHeading() + e.getBearing();
			// enemyBearing = posição absoluta em ângulos do adversário.
			// getHeading = direção de movimento do GODOFREDO.
			// e.getBearing = posição em ângulos do adversário em relação ao GODOFREDO.
			enemyX = getX() + e.getDistance() * Math.sin(Math.toRadians(enemyBearing));
			enemyY = getY() + e.getDistance() * Math.cos(Math.toRadians(enemyBearing));
			// ^ coordenada + (distância do adversário em relação ao GODOFREDO * proporção da distância)
			enemyHeading = e.getHeading();	// posição que o corpo do adversário está olhando.
			enemyVelocity = e.getVelocity();// velocidade do adversário.

			// TENTA PREVER a próxima posição do adversário.
			// calculando a velocidade da bala.
			double bulletSpeed = 20 - 3 * bulletPower;
			// inicializando a previsão como a posição atual do adversário.
			double predictedX = enemyX;
			double predictedY = enemyY;
			double count = 0; // contador.

			while ((++count) * bulletSpeed < Math.hypot(predictedX - getX(), predictedY - getY())) {
				// o laço de repetição continua a funcionar enquanto a bala, viajando em bulletSpeed, levar menos tempo
				// para alcançar a posição prevista do que o tempo que leva para o inimigo se mover para a posição prevista.
				predictedX += Math.sin(Math.toRadians(enemyHeading)) * enemyVelocity;
				predictedY += Math.cos(Math.toRadians(enemyHeading)) * enemyVelocity;
				// ^ ATUALIZA a posição prevista de acordo com a velocidade e direção de movimento do adversário.

				// garante que as posições de previsão permaneçam dentro dos limites da arena, quanto mantem uma distancia das paredes.
				predictedX = Math.max(Math.min(predictedX, getBattleFieldWidth() - 18), 18);
				predictedY = Math.max(Math.min(predictedY, getBattleFieldHeight() - 18), 18);
			}

			// APONTA a arma para a posiçao prevista do adversário.
			double gunTurn = Utils.normalRelativeAngleDegrees(Math.toDegrees(Math.atan2(predictedX - getX(), predictedY - getY())) - getGunHeading());
			// ^ RETORNA o valor da distância em ângulos do GODOFREDO para a posição prevista.
			setTurnGunRight(gunTurn);
			setFire(bulletPower);

			// MAPEIA o adversário outra vez.
			setTurnRight(e.getBearing());
			// ^ e.getBearing = posição em ângulos do adversário em relação ao GODOFREDO.
			setAhead(e.getDistance() - 140);
			// ^ e.getDistance = posição do adversário em relação ao GODOFREDO.
			// ^ MANTEM uma distância de 140 unidades.

			// AJUSTA o radar para mapear o adversário
			double radarTurn = Utils.normalRelativeAngleDegrees(enemyBearing - getRadarHeading());
			// ^ CALCULA a diferença entre a posição relativs em ângulos do adversário e a direção do radar do GODOFREDO.
			setTurnRadarRight(radarTurn);

		} // fecha OFENSIVA TRACKER.

	}// fecha método onScannedRobot.



	/**
	 * onHitRobot:  Set him as our new target
	 */
	public void onHitRobot(HitRobotEvent e) {

		/**
		 *    DEFENSIVA : WALLS!
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

			// MOVE o GODOFREDO perpendicular a direção de movimento das balas.
			setTurnRight(normalizeBearing(e.getBearing() + 90));
			// ^ posição em ângulos do adversário em relação ao GODOFREDO + 90º (perpendicular).
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

			// AFASTA o GODOFREDO da parede perpendicularmente.
			setTurnRight(normalizeBearing(180 - getHeading()));
			setAhead(100);

		} // fecha OFENSIVA TRACKER.

	} // fecha método onHitWall.



	/**
	 * onWin: GODOFREDO dança ao vencer.
	 */
	public void onWin(WinEvent e) {

		System.out.println(); // DEBUG.
		for (int i = 0; i < 50; i++) {
			turnRight(40);
			turnLeft(40);
			// ESTER EGG:
			System.out.println("ESTEREGG DE VITÓRIA.");
		}

	} // fecha método onWin.



	/**
	 * MÉTODO DE AUXÍLIO para normalizar o ângulo relativo entre +180 e -180.
	 * (impede que os ângulos somem uns aos outros e faça o GODOFREDO girar de mais na arena).
	 */
	double normalizeBearing(double angle) {
		while (angle > 180) {
			angle -= 360;
		}
		
		while (angle < -180) {
			angle += 360;
		}
		
		return angle;
		
	}

} // fecha classe SUPERGODOFREDO
