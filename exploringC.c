/**
 * First C program - a compilation of 10 smaller programs that can be called at the user's request
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <math.h>
#include <limits.h>

#define TRUE 1;
#define FALSE 0;

double compute_pi(int n)
{
	double guess = 0;

	for (int i = 0; i < n; i++)
	{
		if (i % 2 == 0) // even iterations
			guess += 1.0 / (1 + (2 * i));
		else // odd iterations
			guess -= 1.0 / (1 + (2 * i));
	}
	return 4 * guess; // finish calculation
}

double compute_sqrt(double x)
{
	double guess = 1;

	for (int i = 0; i < 10; i++) // run calculation 10 times
		guess = 0.5 * (guess + (x / guess));

	return guess;
}

int is_prime(int n)
{
	for (int i = 2; i <= sqrt(n); i++)
		if (n % i == 0) // if n divisible by i not prime
			return FALSE;

	return TRUE;
}

void display_primes(int n)
{
	for (int i = 2; i <= n; i++)
		if (is_prime(i)) // if prime print i
			printf("%d ", i);
	printf("\n");
}

void process_scores()
{
	int sum = 0;
	int count = 0;

	char min_name[40];
	char max_name[40];
	int min_score = INT_MAX;
	int max_score = 0;
	
	char temp[40];
	int temp_score;

	int go = TRUE;
	while (go) // loops until user enters q
	{
		printf("Enter name and score separated by a space, or q to quit: ");
		gets(temp); // get string from user

		if (strcmp(temp, "q") == 0)
		{
			go = FALSE; // end loop
		}
		else
		{
			int space = 0;
			while (temp[space] != ' ') // find index of space char
				space++;

			int place = 1;
			temp_score = 0;
			// convert str to int
			for (int i = strlen(temp) - 1; i > space; i--) // start from end
			{
				// add ascii-int conversion times place value
				temp_score += (temp[i] - 48) * place;
				place *= 10; // increase to next power of ten
			}

			temp[space] = 0; // replace space with null char

			sum += temp_score; // add score to sum
			count++; // increase count

			if (temp_score < min_score) // replace if min
			{
				min_score = temp_score;
				strcpy(min_name, temp);
			}
			if (temp_score > max_score) // replace if max
			{
				max_score = temp_score;
				strcpy(max_name, temp);
			}
		}
	}
	printf("\nAverage score: %f\n", (double) sum / count);
	printf("Max score is %d by %s\n", max_score, max_name);
	printf("Min score is %d by %s\n", min_score, min_name);
}

double compute_tax(int income, char *status, char state)
{
	double tax;
	double deduction;

	if (income < 0) // invalid input if negative
		return -1;

	if (tolower(state) == 'o') // outstate gets 3% deduction
	{
		deduction = 0.03;
	}
	else if (tolower(state) == 'i') // instate no deduction
	{
		deduction = 0;
	}
	else // invalid input
		return -1;

	// determine correct tax calculation
	if (strcmp(status, "single") == 0)
	{
		if (income < 30000)
		{
			tax = income * (0.2 - deduction);
		}
		else
		{
			tax = income * (0.25 - deduction);
		}
	}
	else if (strcmp(status, "married") == 0)
	{
		if (income < 50000)
		{
			tax = income * (0.1 - deduction);
		}
		else
		{
			tax = income * (0.15 - deduction);
		}
	}
	else // invalid input
		return -1;

	return tax;
}

int quadratic(double a, double b, double c, double *solution1, double *solution2)
{
	double temp = (b * b) - (4 * a * c);

	if (temp >= 0) // calc solutions if exists
	{
		*solution1 = ((-1 * b) + sqrt(temp)) / (2 * a);
		*solution2 = ((-1 * b) - sqrt(temp)) / (2 * a);
		return TRUE;
	}
	else // set to zero if no solution
	{
		*solution1 = 0;
		*solution2 = 0;
		return FALSE;
	}
}

int factorial(int n)
{
	if (n == 0) // base case
		return 1;
	else
		return n * factorial(n - 1); // recursive call
}

void file_count(char *file, int *characters, int *lines)
{
	int chr = 0;
	int lns = 1; // account for first line

	FILE *ptr = fopen(file, "r");

	char temp;
	while ((temp = getc(ptr)) != EOF) // assign each char to temp, check for EOF
	{
		chr++; // count each char
		if (temp == '\n') // count newlines
			lns++;
	}
	// assign values
	*characters = chr;
	*lines = lns;

	fclose(ptr);
}

void file_sort(char *infile, char *outfile)
{
	FILE *iptr;
	FILE *optr;
	int num;

	iptr = fopen(infile, "r");
	
	fscanf(iptr, "%d", &num); // read number of students
	
	// allocate memory for arrays
	int *id = (int *) malloc(num * sizeof(int));
	char *grade = (char *) malloc(num * sizeof(char));
	double *gpa = (double *) malloc(num * sizeof(double));
	
	for (int i = 0; i < num; i++) // read each line in file
		fscanf(iptr, "%d %c %lf", &id[i], &grade[i], &gpa[i]);
	
	fclose(iptr);
	
	optr = fopen(outfile, "w");
	
	// find min and max student ID
	int nxtmin;
	int curmin = 0;
	int max = 0;
	for (int i = 0; i < num; i++)
	{
		if (id[i] < id[curmin])
			curmin = i;
		if (id[i] > id[max])
			max = i;
	}

	// write info to output file in order of ID
	for (int i = 0; i < num; i++)
	{
		// write the current min
		fprintf(optr, "%d %c %.2f\n", id[curmin], grade[curmin], gpa[curmin]);
		
		nxtmin = max;
		for (int j = 0; j < num; j++) // search for next min
			// must be larger than curmin and less than nxtmin to replace nxtmin
			if (id[j] < id[nxtmin] && id[j] > id[curmin])
				nxtmin = j;
		
		curmin = nxtmin; // replace current min
	}
	fclose(optr);
	free(id);
	free(grade);
	free(gpa);
}

void file_student(char *infile)
{
	typedef struct {
		char name[20];
		int age;
		double gpa;
	} info;

	FILE *ptr = fopen(infile, "r");
	int num;

	fscanf(ptr, "%d", &num); // read number of lines

	info *stu = (info *) malloc(num * sizeof(info)); // allocate array of info

	for (int i = 0; i < num; i++) // read each line from file
		fscanf(ptr, "%s %d %lf", stu[i].name, &(stu[i].age), &(stu[i].gpa));

	fclose(ptr);

	// add all gpas then calculate and print average
	double sum = 0;
	for (int i = 0; i < num; i++)
		sum += stu[i].gpa;
	printf("Average GPA: %.2f\n", sum / num);

	// loop through all and print names with gpa >= 2
	printf("\nStudents with GPA greater than 2:\n");
	for (int i = 0; i < num; i++)
		if (stu[i].gpa >= 2)
			printf("%s\n", stu[i].name);

	// find min and max student name
	int nxtmin;
	int curmin = 0;
	int max = 0;
	for (int i = 0; i < num; i++)
	{
		if (strcmp(stu[i].name, stu[curmin].name) < 0)
			curmin = i;
		if (strcmp(stu[i].name, stu[max].name) > 0)
			max = i;
	}

	// print info in lexicographic order of student name
	printf("\nStudent info (alphabetic order):\n");
	for (int i = 0; i < num; i++)
	{
		// print current min
		printf("%s %d %.2f\n", stu[curmin].name, stu[curmin].age, stu[curmin].gpa);
		
		nxtmin = max;
		for (int j = 0; j < num; ++j) // search for next min
			if (strcmp(stu[j].name, stu[nxtmin].name) < 0
				&& strcmp(stu[j].name, stu[curmin].name) > 0)
				nxtmin = j;
		
		curmin = nxtmin; // replace current min
	}
	free(stu);
}

int main()
{
	int input;
	
	int go = TRUE;
	while (go) // loops continues until user quits
	{
		printf("\n1-computing pi\n2-computing square root\n");
		printf("3-displaying primes\n4-processing grades\n");
		printf("5-computing tax\n6-solving quadratic\n");
		printf("7-computing factorial\n8-counting file\n");
		printf("9-sorting file\n10-student file\n11-quit\n");
		printf("\nEnter an option number: ");

		scanf("%d", &input); // get int input from user
		printf("\n");

		if (input == 11)
		{
			go = FALSE; // end loop
		}
		// call a function from 1 through 10 depending on input
		// gets relevant inputs from user and prints output messages
		else if (input == 1)
		{
			int n;

			printf("The number of terms: ");
			scanf("%d", &n);
			printf("Pi calculated using %d terms is %f\n", n, compute_pi(n));
		}
		else if (input == 2)
		{
			int n;

			printf("Calculate the square root of: ");
			scanf("%d", &n);
			printf("Square root of %d is %f\n", n, compute_sqrt(n));
		}
		else if (input == 3)
		{
			int n;

			printf("Display primes less than or equal to: ");
			scanf("%d", &n);
			display_primes(n);
		}
		else if (input == 4)
		{
			getchar(); // clear input buffer
			process_scores();
		}
		else if (input == 5)
		{
			int income;
			char status[8], state;
			double tax;

			printf("Enter tax info separated by spaces (income status state): ");
			scanf("%d %s %c", &income, status, &state);
			tax = compute_tax(income, status, state);
			if (tax == -1)
				printf("Invalid input\n");
			else
				printf("Tax: $%.2f\n", compute_tax);
		}
		else if (input == 6)
		{
			double a, b, c, s1, s2;

			printf("Enter coefficients separated by spaces (a b c): ");
			scanf("%lf %lf %lf", &a, &b, &c);
			quadratic(a, b, c, &s1, &s2);
			printf("Solution 1: %f\nSolution 2: %f\n", s1, s2);
		}
		else if (input == 7)
		{
			int n;

			printf("Calculate factorial of: ");
			scanf("%d", &n);
			printf("!%d = %d\n", n, factorial(n));
		}
		else if (input == 8)
		{
			char infile[60];
			int chars, lines;

			printf("Enter file name: ");
			scanf("%s", infile);
			file_count(infile, &chars, &lines);
			printf("Characters: %d\nLines: %d\n", chars, lines);
		}
		else if (input == 9)
		{
			char infile[60], outfile[60];

			printf("Enter input file name: ");
			scanf("%s", infile);
			printf("Enter output file name: ");
			scanf("%s", outfile);
			file_sort(infile, outfile);
			printf("Output file written\n");
		}
		else if (input == 10)
		{
			char infile[60];

			printf("Enter file name: ");
			scanf("%s", infile);
			file_student(infile);
		}
		else
			printf("Invalid input. Try again.\n");
	}
	return 0;
}
