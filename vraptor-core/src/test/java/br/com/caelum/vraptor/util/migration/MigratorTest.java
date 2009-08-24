package br.com.caelum.vraptor.util.migration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

public class MigratorTest {

	private Mockery mockery;
	private ConnectionProvider provider;
	private Migrator migrator;
	private Migration first;
	private Migration second;

	@Before
	public void setup() {
		this.mockery = new Mockery();
		this.provider = mockery.mock(ConnectionProvider.class);
		this.first = mockery.mock(Migration.class, "first");
		this.second = mockery.mock(Migration.class, "second");
		List<Migration> list = Arrays.asList(this.first, this.second);
		this.migrator = new Migrator(provider, new ArrayBasedMigrationProvider(list));
		mockery.checking(new Expectations() {
			{
				allowing(first).getId();
				will(returnValue("first"));
				allowing(second).getId();
				will(returnValue("second"));
			}
		});
	}

	@Test
	public void shouldDoNothingIfDatabaseHasAllMigrations() {
		mockery.checking(new Expectations() {
			{
				one(provider).getAppliedMigrations();
				will(returnValue(Arrays.asList("second", "first")));
			}
		});
		Migrations migrationsToApply = migrator.getMigrationsToApply();
		assertThat(migrationsToApply.size(), is(equalTo(0)));
		mockery.assertIsSatisfied();
	}

	@Test
	public void shouldReturnNonExecutedMigrationsIfThereAreAnyPending() {
		mockery.checking(new Expectations() {
			{
				one(provider).getAppliedMigrations();
				will(returnValue(Arrays.asList("first")));
			}
		});
		Migrations migrationsToApply = migrator.getMigrationsToApply();
		assertThat(migrationsToApply.size(), is(equalTo(1)));
		assertThat(migrationsToApply.getAll().iterator().next(), is(equalTo(second)));
		mockery.assertIsSatisfied();
	}

}
