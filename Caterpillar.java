package assignment2;

import java.awt.Color;
import java.util.Random;

import assignment2.food.*;


public class Caterpillar {
	// All the fields have been declared public for testing purposes
	public Segment head;
	public Segment tail;
	public int length;
	public EvolutionStage stage;

	public MyStack<Position> positionsPreviouslyOccupied;
	public int goal;
	public int turnsNeededToDigest;


	public static Random randNumGenerator = new Random(1);


	// Creates a Caterpillar with one Segment. It is up to students to decide how to implement this. 
	public Caterpillar(Position p, Color c, int goal) {
		/*
		 * TODO: ADD YOUR CODE HERE
		 */
		this.head = new Segment(p, c);
		this.tail = this.head;
		this.length = 1;

		this.goal = goal;
		this.stage = EvolutionStage.FEEDING_STAGE;
		this.turnsNeededToDigest = 0;

		this.positionsPreviouslyOccupied = new MyStack<>();
	}

	public EvolutionStage getEvolutionStage() {
		return this.stage;
	}

	public Position getHeadPosition() {
		return this.head.position;
	}

	public int getLength() {
		return this.length;
	}


	// returns the color of the segment in position p. Returns null if such segment does not exist
	public Color getSegmentColor(Position p) {
		/*
		 * TODO: ADD YOUR CODE HERE
		 */
		Segment curr = this.head;

		while(curr != null) {
			if(curr.position.equals(p)){
				return curr.color;
			}
			curr = curr.next;
		}

		return null;
	}
	
	
    // Methods that need to be added for the game to work
    public Color[] getColors(){
        Color[] cs = new Color[this.length];
        Segment chk = this.head;
        for (int i = 0; i < this.length; i++){
            cs[i] = chk.color;
            chk = chk.next;
        }
        return cs;
    }

    public Position[] getPositions(){
        Position[] ps = new Position[this.length];
        Segment chk = this.head;
        for (int i = 0; i < this.length; i++){
            ps[i] = chk.position;
            chk = chk.next;
        }
        return ps;
    }

    
	// shift all Segments to the previous Position while maintaining the old color
	// the length of the caterpillar is not affected by this
	public void move(Position p) {
		/*
		 * TODO: ADD YOUR CODE HERE
		 */

		if (Position.getDistance(this.head.position, p) != 1) {
			throw new IllegalArgumentException("input position not orthogonally connected to head's position");
		}

		Segment curr = this.head;
		while (curr != null) {
			if (curr.position.equals(p)) {
				this.stage = EvolutionStage.ENTANGLED;
				return;
			}
			curr = curr.next;
		}

		if(this.head.next == null){
			this.positionsPreviouslyOccupied.push(this.tail.position);
			this.head.position = p;
			this.tail.position = p;
			return;
		}

		this.positionsPreviouslyOccupied.push(this.tail.position);

		Position temp1 = this.head.position;
		this.head.position = p; //new position, s0
		curr = this.head.next;
		while(curr != null){
			Position temp2 = curr.position; //s2 spot
			curr.position = temp1; //s2 changed to s1
			temp1 = temp2; //s1 changed to s2
			curr = curr.next;
		}

		if(curr == null){
			this.tail.position = temp1;
		}
		fixThis();
		this.length++;
		if (this.turnsNeededToDigest <= 0 && this.stage != EvolutionStage.BUTTERFLY) {
			this.stage = EvolutionStage.FEEDING_STAGE;
		}
	}

	// a segment of the fruit's color is added at the end
	public void eat(Fruit f) {
		/*
		 * TODO: ADD YOUR CODE HERE
		 */

		Position newTailPosition = this.positionsPreviouslyOccupied.pop();
		Segment newTail = new Segment(newTailPosition, f.getColor());

		if(this.head.next == null){
			this.head.next = newTail;
		}

		this.tail.next = newTail;
		this.tail = newTail;
		this.length++;

		if (this.length >= this.goal) {
			this.stage = EvolutionStage.BUTTERFLY;
		}
	}

	// the caterpillar moves one step backwards because of sourness
	public void eat(Pickle p) {
		/*
		 * TODO: ADD YOUR CODE HERE
		 */

		/*Position previous = this.head.position;

		Segment curr = this.head;
		while (curr != null && curr.next != null) {
			curr.position = curr.next.position;
			curr = curr.next;
		}

		this.head.position = previous;
		this.positionsPreviouslyOccupied.push(this.tail.position);*/

		Segment curr = this.head;
		while(curr != null && curr.next != null){
			curr.position = curr.next.position;
			curr = curr.next;
		}

		Position lastPreviouslyOccupied = this.positionsPreviouslyOccupied.pop();
		this.tail.position = lastPreviouslyOccupied;

		/*if(this.head.next != null){
			this.head.position = this.head.next.position;
		}
		this.positionsPreviouslyOccupied.push(this.tail.position);*/
	}

	// all the caterpillar's colors shuffle around
	public void eat(Lollipop lolly) {
		/*
		 * TODO: ADD YOUR CODE HERE
		 */

		Color[] caterpillarColors = getColors();

		for (int j = caterpillarColors.length - 1; j > 0; j--) {
			int newIndex = randNumGenerator.nextInt(j + 1);

			Color temp = caterpillarColors[j];
			caterpillarColors[j] = caterpillarColors[newIndex];
			caterpillarColors[newIndex] = temp;
		}

		Segment curr = this.head;
		int i = 0;
		while (curr != null) {
			curr.color = caterpillarColors[i];
			curr = curr.next;
			i++;
		}
	}

	// brain freeze!!
	// It reverses and its (new) head turns blue
	public void eat(IceCream gelato) {
		/*
		 * TODO: ADD YOUR CODE HERE
		 */

		Segment previous1 = null;
		Segment curr2 = this.head;
		Segment next3;

		while (curr2 != null) {
			next3 = curr2.next;
			curr2.next = previous1;
			previous1 = curr2;
			curr2 = next3;
		}

		this.head = previous1;
		this.head.color = GameColors.BLUE;

		Segment temp = this.head;
		while(temp.next != null){
			temp = temp.next;
		}
		this.tail = temp;

		this.positionsPreviouslyOccupied.clear();
	}

	// the caterpillar embodies a slide of Swiss cheese loosing half of its segments. 
	public void eat(SwissCheese cheese) {
		/*
		 * TODO: ADD YOUR CODE HERE
		 */

		Segment curr = this.head;
		MyStack<Color> colorStack = new MyStack<>();
		MyStack<Color> anotherColorStack = new MyStack<>();
		MyStack<Position> positionStack = new MyStack<>();

		int index = 0;
		int newLength = 0;
		while(curr != null) {
			if (index % 2 == 0) {
				colorStack.push(curr.color);
				newLength++;
			}
			index++;
			curr = curr.next;
		}

		while(!colorStack.empty()){
			anotherColorStack.push(colorStack.pop());
		}

		curr = this.head;
		while(!anotherColorStack.empty()){
			curr.color = anotherColorStack.pop();
			if(!anotherColorStack.empty()){
				curr = curr.next;
			}
		}

		Segment newCurr = curr;
		curr = curr.next;
		while(curr != null){
			positionStack.push(curr.position);
			curr = curr.next;
		}
		while(!positionStack.empty()){
			positionsPreviouslyOccupied.push(positionStack.pop());
		}
		newCurr.next = null;
		this.tail = newCurr;
		this.length = newLength;
	}

	public void eat(Cake cake) {
		/*
		 * TODO: ADD YOUR CODE HERE
		 */

		if (this.stage == EvolutionStage.BUTTERFLY) {
			return;
		}

		turnsNeededToDigest += cake.getEnergyProvided();

		if (this.stage != EvolutionStage.GROWING_STAGE) {
			this.stage = EvolutionStage.GROWING_STAGE;
		}

		fixThis();

		if (turnsNeededToDigest <= 0 && stage != EvolutionStage.BUTTERFLY) {
			this.stage = EvolutionStage.FEEDING_STAGE;
		}

	}

	public void fixThis(){
		MyStack<Position> positionStack = this.positionsPreviouslyOccupied;
		while (turnsNeededToDigest > 0 && !positionStack.empty()) {
			Position lastPreviouslyOccupied = positionStack.peek();

			Segment checkingSegment = this.head;
			boolean inUse = false;
			while (checkingSegment != null) {
				if (checkingSegment.position.equals(lastPreviouslyOccupied)) {
					inUse = true;
					break;
				}
				checkingSegment = checkingSegment.next;
			}

			if (inUse) {
				return;
			}

			Segment newTail = new Segment(lastPreviouslyOccupied, GameColors.SEGMENT_COLORS[randNumGenerator.nextInt(GameColors.SEGMENT_COLORS.length)]);

			this.tail.next = newTail;
			this.tail = newTail;
			this.length++;
			turnsNeededToDigest--;

			positionStack.pop();

			if (this.length >= this.goal) {
				this.stage = EvolutionStage.BUTTERFLY;
				return;
			}
		}
	}

	// This nested class was declared public for testing purposes
	public class Segment {
		private Position position;
		private Color color;
		private Segment next;

		public Segment(Position p, Color c) {
			this.position = p;
			this.color = c;
		}

	}

	public String toString() {
		Segment s = this.head;
		String snake = "";
		while (s!=null) {
			String coloredPosition = GameColors.colorToANSIColor(s.color) + 
					s.position.toString() + GameColors.colorToANSIColor(Color.WHITE);
			snake = coloredPosition + " " + snake;
			s = s.next;
		}
		return snake;
	}

	public static void main(String[] args) {
		Position startingPoint = new Position(3, 2);
		Caterpillar gus = new Caterpillar(startingPoint, GameColors.GREEN, 10);

		System.out.println("1) Gus: " + gus);
		System.out.println("Stack of previously occupied positions: " + gus.positionsPreviouslyOccupied);

		gus.move(new Position(3,1));
		gus.eat(new Fruit(GameColors.RED));
		gus.move(new Position(2,1));
		gus.move(new Position(1,1));
		gus.eat(new Fruit(GameColors.YELLOW));


		System.out.println("\n2) Gus: " + gus);
		System.out.println("Stack of previously occupied positions: " + gus.positionsPreviouslyOccupied);

		gus.move(new Position(1,2));
		gus.eat(new IceCream());

		System.out.println("\n3) Gus: " + gus);
		System.out.println("Stack of previously occupied positions: " + gus.positionsPreviouslyOccupied);

		gus.move(new Position(3,1));
		gus.move(new Position(3,2));
		gus.eat(new Fruit(GameColors.ORANGE));


		System.out.println("\n4) Gus: " + gus);
		System.out.println("Stack of previously occupied positions: " + gus.positionsPreviouslyOccupied);

		gus.move(new Position(2,2));
		gus.eat(new SwissCheese());

		System.out.println("\n5) Gus: " + gus);
		System.out.println("Stack of previously occupied positions: " + gus.positionsPreviouslyOccupied);

		gus.move(new Position(2, 3));
		gus.eat(new Cake(4));

		System.out.println("\n6) Gus: " + gus);
		System.out.println("Stack of previously occupied positions: " + gus.positionsPreviouslyOccupied);
	}
}