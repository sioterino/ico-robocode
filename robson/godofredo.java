package robson;

import robocode.*;
import robocode.util.Utils;

import java.awt.Color;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * ROBSON GODOFREDO EUGÊNIO - walls tracker defensivo / tracker ofensivo bot por:
 * 												   INGRIDY, SANT e SOFIA.
 *
 * ROBSON = filho da glória ilustre (germanico).
 * GODOFREDO = deus da paz (germanico).
 * EUGÊNIO = nobre (grego/latino).
 **/

public class godofredo extends AdvancedRobot {

	boolean ofensiva = false; // define estratégia de operação: defensiva / ofensiva.

	// declaração de independência *DEFENSIVA*
	boolean check;				// impede o GODOFREDO de se mover caso um robo esteja na sua frente.
	double move;				// define a quantidade de movimento do GODOFREDO dentro da arena.
	double bulletPower;			// inicializa a força dos tiros.
	
	double trackingTime = 0; 	// contador de tempo de rastreamento.
	final long TICKS = 600; 	// tempo máximo de rastreamento defensivo.
	String trackingName = null; // nome do adversário sendo rastreado.
	double enemyX;				// posição HORIZONTAL do adversário na arena.
	double enemyY;				// posição VERTICAL do adversário na arena.
	double enemyHeading;	// direção em que o adversário está olhando.
	double enemyVelocity;	// velocidade do adversário.

	// declaração de independência *OFENSIVA*
  	int gunDirection = 1;



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

			checkOthers(); // TESTANDO quantos adversários ainda estão na arena.
		

			/**
			 *    DEFENSIVA
			 */
			if (!ofensiva) { // WALLS TRACKER!

				System.out.println("DEFENSIVA! \n"); // DEBUG.

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

				turnRadarRight(360);

				while (true) { // laço de repetição WALLS TRACKER.
					check = true;
					ahead(move);
					check = false;
					
           			turnRadarRight(360);
										
					turnRight(90);

					// IMPEDE o GODOFREDO de focar muito tempo em um único adversário.
         		   if (trackingName != null && getTime() - trackingTime > TICKS) {
				        // CASO exista um adversário sendo rastreado e o tempo em ticks tenha excedito
						// os 600 ticks pre estabelecidos, reseta as variáveis de rastreamento.
						trackingName = null;
				        trackingTime = 0;
				        System.out.println("PROCURANDO NOVO ALVO. \n");
						
						turnRadarRight(360);
						scan();
				    }

					checkOthers(); // TESTANDO quantos adversários ainda estão na arena.

					execute();

				} // fecha laço de repetição WALLS TRACKER.

			} // fecha DEFENSIVA



			/**
			 *    OFENSIVA
			 */
			if (ofensiva) { // TRACKER!

				while (true) { // laço de repetição TRACKER
				
					turnGunRight(360);
					
				} // fecha laço de repetição TRACKER

			} // fecha OFENCIVA TRACKER.

		} // fecha laço de repetição RUN.

	} // fecha método RUN.


	/**
	 * onScannedRobot: comportamento do GODOFREDO ao scannear um bot.
	 */
	public void onScannedRobot(ScannedRobotEvent e) {

		// definindo a força do tiro de acordo com a energia do GODOFREDO.

		/**
		 *    DEFENSIVA : WALLS TRACKER!
		 */
		if (!ofensiva) { // WALLS TRACKER!

			// Atualiza o tempo de início de foco
		    if (trackingName == null || !trackingName.equals(e.getName())) {
				// caso não exista um alvo sendo rastreado ou o alvo rastreado não é igual ao adversário atual, mude de alvo.
		        trackingName = e.getName();
		        trackingTime = getTime();
				System.out.println("RASTREANDO " + trackingName + ". \n");
		    }
	
	        // Calcula o tempo estimado para o tiro atingir o alvo
	        bulletPower = calculateFirePower(e.getDistance());
	        double bulletSpeed = 20 - 3 * bulletPower;
	        double bulletTravelTime = e.getDistance() / bulletSpeed;
			// retorna o valor da energia empregada no tiro.
			System.out.println("ENERGIA DO GODOFREDO: " + getEnergy() + ".");
			System.out.println("FORÇA DO TIRO: " + bulletPower + ". \n");

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
	        turnGunRight(gunTurn);
	
			// ATIRA levando em consideração a distância do inimigo.
	        if (getGunHeat() == 0) {
	            setFire(bulletPower);
	        }
			
		} // fecha DEFENSIVA WALLS TRACKER.



		/**
		 *    OFENSIVA: ADVENCED TRACKER BOT!
		 */
		if (ofensiva) { // TRACKER!

		    // gira o GODOFREDO na direção do adversário.
		    setTurnRight(e.getBearing());
		    // atira sempre que estiver com o adversário na mira.
	        setFire(bulletPower);
			
		    setAhead(100);
		    // INVERTE a direção da arma todo o tempo.
		    gunDirection = -gunDirection;
		    // gira 360º graus em sentidos: horário e anti horário.
		    setTurnGunRight(360 * gunDirection);

		    execute();

		} // fecha OFENSIVA TRACKER.

	}// fecha método onScannedRobot.



	/**
	 * onHitRobot:  Set him as our new target
	 */
	public void onHitRobot(HitRobotEvent e) {

		/**
		 *    DEFENSIVA : WALLS TRACKER!
		 */
		if (!ofensiva) { // WALLS TRACKER!

	        // faz GODOFREDO apontar a arma ao adversário e atirar.
	        double gunTurn = Utils.normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getGunHeading()));
	        turnGunRight(gunTurn);
			
			// ATIRA levando em consideração a distância do inimigo.
            setFire(bulletPower);

			if (e.getBearing() > -90 && e.getBearing() < 90) {
				// se o bot estiver na frente do GODOFREDO:
				back(100);
			}
			else {
				// se não...
				ahead(100);
			}

		} // fecha DEFENSIVA WALLS TRACKER.

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
	//		setTurnRight(normalizeBearing(e.getBearing() + 90));
			// ^ posição em ângulos do adversário em relação ao GODOFREDO + 90º (perpendicular).
	//		setAhead(100);

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
	 * onRobotDeath: GODOFREDO reseta o rastreamento.
	 */
    public void onRobotDeath(RobotDeathEvent e) {
       
	 	if (e.getName().equals(trackingName)) {
            // muda de alvo caso este já tenha sido morto.
            trackingName = null;
            trackingTime = 0;
        }
		
    } // fecha método onRobotDeath.


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
		
	} // fecha MÉTODO DE AUXÍLIO.
	
	/**
	 * MÉTODO DE AUXÍLIO para calcular força do tiro de acordo
	 * com a distância do adversário em relação ao GODOFREDO.
	 */
    public double calculateFirePower(double distance) {		

		if (distance < 600) {
			// PERTO = FORTE
            return 3.0;
			
        } else if (distance < 700) {
			// MEIO PERTO = MEIO FORTE
            return 2.0;
			
        } else if (distance < 800) {
			// LONGE = FRACO
            return 1.0;
			
        } else {
			// ALÉM DO ALCANCE.
            return 0.1;
        }
		
    } // fecha MÉTODO DE AUXÍLIO.
	


	/**
	 * MÉTODO DE AUXÍLIO que confere quantos robos
	 * ainda estão vivos na arena junto do GODOFREDO.
	 */
    public void checkOthers() {
        
			if (getOthers() == 1) {
				System.out.println("LAST STANDING TRIBUTE: ofensiva! \n");
				ofensiva = true;
		
				// colorindo o GODOFREDO (ofensiva tracker):
				setBodyColor(Color.black);		// CORPOR: preto.
				setGunColor(Color.red);			// ARMA: vermelho.
				setRadarColor(Color.orange);	// RADAR: laranja.
				setBulletColor(Color.gray);		// BALA: cinza.
				setScanColor(Color.red);		// SCANNER: vermelho.
				
			} // fecha laço condicional (getOthers == 1).

    } // fecha MÉTODO DE AUXÍLIO.

} // fecha classe SUPERGODOFREDO
