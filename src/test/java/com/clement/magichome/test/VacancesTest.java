package com.clement.magichome.test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.clement.magichome.object.BonPoint;
import com.clement.magichome.object.Vacances;
import com.clement.magichome.scheduler.DayScheduler;
import com.clement.magichome.service.LogRepositoryImpl;
import com.clement.magichome.service.VacancesRepository;
import com.clement.magichome.service.VacancesService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class VacancesTest {

	static final Logger LOG = LoggerFactory.getLogger(VacancesTest.class);

	DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	@Autowired
	VacancesService vacancesService;

	@Autowired
	VacancesRepository vacancesRepository;
	@Autowired
	LogRepositoryImpl logRepository;

	@Autowired
	DayScheduler dayScheduler;

	private void insertDataset1() throws Exception {
		vacancesRepository.deleteAll();
		vacancesRepository.save(new Vacances(df.parse("2016-11-11"), df.parse("2016-11-11")));
		vacancesRepository.save(new Vacances(df.parse("2016-11-01"), df.parse("2016-11-01")));
		vacancesRepository.save(new Vacances(df.parse("2016-12-19"), df.parse("2017-01-02")));
	}

	@Test
	public void testVacances() throws Exception {
		insertDataset1();
		Vacances vacances = new Vacances();
		Date dateDebut = df.parse("2016-01-01");
		Date dateFin = df.parse("2016-12-31");
		vacances.setDateDebut(dateDebut);
		vacances.setDateFin(dateFin);
		Boolean exist = vacancesService.checkVacanceNotExisting(vacances);
		Assert.assertEquals(true, exist);

		// * Collisison with 01-Jan_2017*/
		dateDebut = df.parse("2017-01-01");
		dateFin = df.parse("2017-12-31");
		vacances.setDateDebut(dateDebut);
		vacances.setDateFin(dateFin);
		exist = vacancesService.checkVacanceNotExisting(vacances);
		Assert.assertEquals(true, exist);
		/* Collisison with 02-Jan_2017 */
		dateDebut = df.parse("2017-01-02");
		dateFin = df.parse("2017-01-02");
		vacances.setDateDebut(dateDebut);
		vacances.setDateFin(dateFin);
		exist = vacancesService.checkVacanceNotExisting(vacances);
		Assert.assertEquals(false, exist);
		/* Collisison with 02-Jan_2017 */
		dateDebut = df.parse("2017-01-01");
		dateFin = df.parse("2017-01-02");
		vacances.setDateDebut(dateDebut);
		vacances.setDateFin(dateFin);
		exist = vacancesService.checkVacanceNotExisting(vacances);
		Assert.assertEquals(true, exist);
	}

}
