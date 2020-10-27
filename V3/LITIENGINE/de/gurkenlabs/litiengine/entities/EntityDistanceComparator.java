package de.gurkenlabs.litiengine.entities;

public class EntityDistanceComparator extends EntityComparator {

	public EntityDistanceComparator(final IEntity relativeEntity) {
		super(relativeEntity);
	}

	@Override
	public int compare(final IEntity entity1, final IEntity entity2) {
		if (this.getRelativeEntity() == null) {
			return 0;
		}

		final double distance1 = entity1.getLocation().distance(this.getRelativeEntity().getLocation());
		final double distance2 = entity2.getLocation().distance(this.getRelativeEntity().getLocation());
		if (distance1 < distance2) {
			return -1;
		}
		if (distance1 > distance2) {
			return 1;
		}

		return 0;
	}
}
