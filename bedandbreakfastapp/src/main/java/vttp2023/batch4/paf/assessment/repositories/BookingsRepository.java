package vttp2023.batch4.paf.assessment.repositories;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import vttp2023.batch4.paf.Exceptions.SQLAddBookingUnsuccessfulException;
import vttp2023.batch4.paf.Exceptions.SQLAddUserUnsuccessfulException;
import vttp2023.batch4.paf.Exceptions.SQLUnsuccessfulException;
import vttp2023.batch4.paf.assessment.models.Bookings;
import vttp2023.batch4.paf.assessment.models.User;

@Repository
public class BookingsRepository {
	
	// You may add additional dependency injections

	public static final String SQL_SELECT_USER_BY_EMAIL = "select * from users where email like ?";

	@Autowired
	private JdbcTemplate template;

	// You may use this method in your task
	public Optional<User> userExists(String email) {
		SqlRowSet rs = template.queryForRowSet(SQL_SELECT_USER_BY_EMAIL, email);
		if (!rs.next()){
			System.out.println("not here");
			return Optional.empty();
		}

		return Optional.of(new User(rs.getString("email"), rs.getString("name")));
	}

	// TODO: Task 6
	// IMPORTANT: DO NOT MODIFY THE SIGNATURE OF THIS METHOD.
	// You may only add throw exceptions to this method
	public void newUser(User user) {
		String SQL_CREATE_USER="""
				INSERT INTO users
				VALUES (?, ?)
				""";
		String name = user.name();
		String email = user.email();
		long updateCount = template.update(SQL_CREATE_USER,email,name);
		if(updateCount<=0){
			throw new SQLAddUserUnsuccessfulException();
		}

	}

	// TODO: Task 6
	// IMPORTANT: DO NOT MODIFY THE SIGNATURE OF THIS METHOD.
	// You may only add throw exceptions to this method
	public void newBookings(Bookings bookings) {
		String booking_id = bookings.getBookingId();
		String listing_id = bookings.getListingId();
		int duration = bookings.getDuration();
		String email = bookings.getEmail();
		String SQL_INSERT_BOOKING = """
				INSERT INTO bookings (booking_id, listing_id, duration, email)
				VALUES (?,?,?,?)
				""";
		long insertCount = template.update(SQL_INSERT_BOOKING,booking_id,listing_id,duration,email);
		if(insertCount<=0){
			throw new SQLAddBookingUnsuccessfulException();
		}

	}
}
