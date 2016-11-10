/*
 * Author: Ahmed Aldhaheri
 * Date:   October 13, 2016
 * Course: COSC 341
 * Description: The program has various functions doing different tasks.
 * 				It asks the user to choose from a menu to perform 
 *				various operations.
*/

/* Include header files */
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>
#include <ctype.h>

#define TRUE 1
#define FALSE 0

/* Declare prototypes */
double compute_pi(int);
double compute_sqrt(double);
int is_prime(int);
void display_primes(int);
void process_scores();
double compute_tax(int, char *, char);
int quadratic(double, double, double, double *, double *);
int factorial(int);
void file_count(char *, int *, int *);
void file_sort(char *, char *);
void file_student(char *);

/* student structure */
struct student{
	
	char name[21];
	int age;
	double gpa;
	
};

int main(){
	
	int user_choice;
	
	do{
		/* display menu and take user choice */
		printf("Choose one of the following options: \n");
		printf("1-Computing pi\n2-Computing square root\n3-Displaying primes\n");
		printf("4-Processing grades\n5-Computing tax\n6-Solving quadratic\n");
		printf("7-Computing factorial\n8-Counting file\n9-Sorting fie\n");
		printf("10-Student file\n11-Quit\n");
		scanf("%d", &user_choice);
		getchar();
		
		/* process user choice */
		switch(user_choice){
			/* compute pi */
			case 1:{ 
				int n;
				printf("Enter the first n terms: ");
				scanf("%d", &n);
				getchar();
				double result = compute_pi(n);
				printf("\nThe first %d terms of pi is: %lf\n\n", n, result);
				break;
			}
			
			/* compute square root */
			case 2:{
				double num;
				printf("Enter number: ");
				scanf("%lf", &num);
				getchar();
				double result = compute_sqrt(num);
				printf("\nThe square root of %.2lf is: %.2lf\n\n", num, result);
				break;
			}
			
			/* display primes */
			case 3:{
				int limit;
				printf("Enter up to what number: ");
				scanf("%d", &limit);
				getchar();
				display_primes(limit);
				printf("\n");
				break;
			}
			
			/* processing scores */
			case 4:
				process_scores();break;
			
			/* compute tax */
			case 5:{
				char status[21];
				int income;
				char residency;
				printf("Enter your income tax: ");
				scanf("%d", &income);
				getchar();
				printf("Enter your martial status: ");
				scanf("%s", status);
				printf("Enter o (for out of state) or i (for in state) resident: ");
				scanf("%c", &residency);
				residency = getchar();
				double tax_rate = compute_tax(income, status, residency);
				printf("\nYour tax rate is: %.2lf\n\n", tax_rate);
				break;
			}
			
			/* slove quadratic equation */
			case 6:{
				double a , b, c, solution1, solution2;
				printf("Enter a, b, and c coefficients seprated by space: ");
				scanf("%lf%lf%lf", &a, &b, &c);
				getchar();
				quadratic(a, b, c, &solution1, &solution2);
				printf("solutions: (%.2lf, %.2lf)\n\n", solution1, solution2);
				break;
			}
			
			/* computes factorial */
			case 7:{
				int n;
				printf("Enter number: ");
				scanf("%d", &n);
				getchar();
				printf("Factorial of %d is: %d\n\n", n, factorial(n));
				break;
			}
			
			/* counts characters in file */
			case 8:{
				char file_name[21];
				int characters, lines;
				printf("Enter file name: ");
				scanf("%s", file_name);
				file_count(file_name, &characters, &lines);
				printf("Number of characters is: %d\n", characters);
				printf("Number of lines is: %d\n\n", lines);
				break;	
			}
			
			/* sorts file */
			case 9:{
				char in_file[21], out_file[21];
				printf("Enter input file: ");
				scanf("%s", in_file);
				printf("Enter output file: ");
				scanf("%s", out_file);
				file_sort(in_file, out_file);
				printf("\n");
				break;
			}
			
			/* read student file */
			case 10:{
				char in_file[21];
				printf("Enter input file: ");
				scanf("%s", in_file);
				file_student(in_file);
				break;
			}
			
			default:printf("Invalid choice\n\n");break;
			
		}
	
	}while(user_choice != 11);
	
	
	return 0;
}/* end main */


/*
 * Function computes the value of pi using the first n terms
 * of the infinite series and return its value
 * @param n the n terms
 * @return value_of_n the value of n terms of the infinite series of pi
*/
double compute_pi(int n){
	
	int k;
	double value_of_n, temp = 0;
	
	/* infinte series of pi from Calculus II */
	for(k = 1; k <= n; k++)
		temp += pow(-1, k + 1) / (2 * k - 1);
	
	value_of_n = 4 * temp;
	return value_of_n;
	
}

/*
 * Function computes the square root of a number and returns it
 * @param x the number to find square root of
 * @return last the square root of x
*/
double compute_sqrt(double x){
	
	int i;
	double last = 1.0;
	
	for(i = 0; i < 10; i++)
		last = 0.5 * (last + x / last);
	
	return last;
	
}

/*
 * Function checks if n is prime or not
 * @param n the integer to checks
 * @return 0 if false or 1 if true
*/
int is_prime(int n){
	
	if(n <= 1)
		return 0;
	
	int i;
	for(i = 2; i * i <= n; i++)
		if(n % i == 0)
			return 0;
	
	return 1;
}

/* 
 * Function displays all prime numbers to
 * integer n using is_prime helper method
 * @param n the integer n
*/
void display_primes(int n){
	
	int i;
	for(i = 0; i <= n; i++)
		if(is_prime(i))
			printf("%d\n", i);
}

/* 
 * Function reads students names and scores
 * and displays: average, minimum, and maximum 
 * scores. Also student names of max and min scores. 
*/
void process_scores(){

	char input[21], max_name[21], min_name[21], quit;
	double score, max_score, min_score, total = 0;
	int count = 0, name_size = 21;

    do{

        printf("Please enter student name and score separated by space or enter q to quit: ");
        quit = getchar();
        if(quit == 'q' || quit == 'Q')
            break;
        input[0] = quit;
        scanf("%[^ ] %lf", &input[1], &score);
        getchar();

        if(count == 0){
            max_score = score; min_score = score;
            strncpy(max_name, input, name_size);
            strncpy(min_name, input, name_size);
        }

        if(max_score < score){
            max_score = score;
            strncpy(max_name, input, name_size);
        }
        else if(min_score > score){
            min_score = score;
            strncpy(min_name, input, name_size);
        }
		
        count++; total += score;

		}while(1);

		printf("\nThe maximum score is: %.2lf\n", max_score);
		printf("The minimum score is: %.2lf\n", min_score);
		printf("The name of student who recieved maximum score is: %s\n", max_name);
		printf("The name of student who recieved minimum score is: %s\n", min_name);
		printf("The average of scores is: %.2lf\n\n", total / count);


}

/* 
 * Function takes income, marital status, and state  residency and computes tax
 * @param income the income to compute
 * @param status the martial status
 * @param state the state residency
 * @return -1 for invalid input 
 * @return tax_rate the amount income being taxed for
*/
double compute_tax(int income, char *status, char state){
	
	double tax_rate = 0;
	state = toupper(state);
	
	/* make status string uppercase */
	int i = 0;
	while(status[i]){
		status[i] = toupper(status[i]);
		i++;
	}
	
	/* start with data validation */
	if(income <= 0)
		return -1;
	if(state != 'O' && state != 'I')
		return -1;
	if(strcmp(status, "MARRIED") != 0 && strcmp(status, "SINGLE") != 0)
		return -1;
	/* end data validation */
	
	/* if single */
	if(strcmp(status, "SINGLE") == 0){
		
		if(income < 30000)
			tax_rate = income * 0.2;
		else
			tax_rate = income * 0.25;
				
	}/* end if single */
	
	/* if married */
	else{
		
		if(income < 50000)
			tax_rate = income * 0.1;
		else
			tax_rate = income * 0.15;
		
	}/* end if married */
	
	/* if out of state subtract 0.03 (3%) */
	if(state == 'O')
		tax_rate = (tax_rate / income - 0.03) * income;
	
	return tax_rate;
	
}

/* 
 * Function solves a quadratic equation
 * @param a the first coefficient for ax^2
 * @param b the second coefficient bx
 * @param c the third coefficient cx^0
 * @param *solution1 store first solution
 * @param *soultion2 store second solution
 * @return 0 equation has no solutions
 * @return 1 equation has solutions
*/
int quadratic(double a, double b, double c, double *solution1, double *solution2){
	
	/* check if equation has no real solutions return 0 */
	if((b * b) - (4 * a * c) < 0){
		*solution1 = 0; *solution2 = 0;
		return 0;
	}
	
	/* if equation has real solutions */
	*solution1 = (-b + compute_sqrt(b * b - 4 * a * c)) / (2 * a);
	*solution2 = (-b - compute_sqrt(b * b - 4 * a * c)) / (2 * a);
	return 1;
	
} 

/* 
 * Function takes a number and returns its factorial
 * @param n the number
 * @return n if n reduced to 1 then return result
 * @return n * factorial(n - 1) if n > 1 compute n * (n - 1) * (n - 2)...(n - (n - 1))
*/
int factorial(int n){
	
	if(n == 1)
		return n;
	return n * factorial(n - 1);
	
} 

/* 
 * Function coounts the characters and lines in a file_count
 * @param *file the file pointer
 * @param *characters the character count
 * @param *lines the lines count
*/
void file_count(char *file, int *characters, int *lines){
	
	/* declare file pointer and open file */
	FILE *ifp;
	ifp = fopen(file, "r");
	if(ifp == NULL){
		perror("Error");
		exit(1);
	}
	
	/* read file new line ascii is 10*/
	int symbol;
	while(symbol = fgetc(ifp)){
		
		if(symbol == EOF){
			*lines += 1;
			break;
		}
		else if(symbol == 10)
			*lines += 1;
		*characters += 1;
	}
	
	fclose(ifp);
	
}

/*
 * Function reads students data from input file and sorts
 * them in ascending order by student ID and writes sorted 
 * data to output file
 * @param *infile the input 
 * @param *outfile the output file to write to
*/
void file_sort(char *infile, char *outfile){
	
	/* Create file pointer and open file */
	FILE *ifp = fopen(infile, "r");
	FILE *ofp = fopen(outfile, "w");
	if(ifp == NULL){
		perror("Error");
		exit(1);
	}
	
	/* read file to find how many students in file */
	int num_of_students;
	fscanf(ifp, "%d", &num_of_students);
	fgetc(ifp);		/* read new line */
	
	/* allocate memory with 3 array of size num_of_students */
	int *id; char *grade; double *gpa;
	id = (int *) malloc(num_of_students * sizeof(int));
	grade = (char *) malloc(num_of_students * sizeof(char));
	gpa = (double *) malloc(num_of_students * sizeof(double));
	
	/* start reading data from file */
	int i = 0, j = 0;
	while(i < num_of_students){
		
		fscanf(ifp, "%d", &id[i]);
		fgetc(ifp);		/* read space */
		grade[i] = fgetc(ifp);
		fgetc(ifp);	    /* read space */
		fscanf(ifp, "%lf", &gpa[i]);
		i++;
	}
	
	/* sort student information by id using insertion sort */
	for(i = 1; i < num_of_students; i++){
		for(j = i; j > 0; j--){
			if(id[j] < id[j - 1]){
				/* swap id's */
				int temp_int = id[j];
				id[j] = id[j - 1];
				id[j - 1] = temp_int;
				
				/* swap grades */
				char temp_char = grade[j];
				grade[j] = grade[j - 1];
				grade[j - 1] = temp_char;
				
				/*swap gpa's */
				double temp_double = gpa[j];
				gpa[j] = gpa[j - 1];
				gpa[j - 1] = temp_double;
				
			}
		}
	}
	
	/* write to output file */
	fprintf(ofp, "%d", num_of_students);	/* write num of students */
	putc(10, ofp); 						/* writes new line */
	for(i = 0; i < num_of_students; i++){
		fprintf(ofp, "%d%c%c%c%.2lf", id[i], ' ', grade[i], ' ', gpa[i]);
		putc(10, ofp);						/* writes new line */
		
	}
	
	
	free(id); free(gpa); free(grade);
	fclose(ifp); fclose(ofp);
	
	
}

/* 
 * Function reads file containing student information.
 * computes gpa average and displays students with gpa
 * at least 2.Also prints all information in ascending 
 * order of names.
 * @param infile the input file name
 */
 void file_student(char *infile){
	 
	 /* Create file pointer and open file */
	FILE *ifp = fopen(infile, "r");
	if(ifp == NULL){
		perror("Error");
		exit(1);
	}
	
	/* read file to find how many students in file */
	int num_of_students;
	fscanf(ifp, "%d", &num_of_students);
	fgetc(ifp);		/* read new line */
	
	/* creat an array of structs of size num_of_students */
	struct student *info = (struct student *) malloc(num_of_students * sizeof(struct student));
	
	/* start reading from file */
	int i = 0, j = 0; double gpa_sum = 0;
	while(i < num_of_students){
		
		fscanf(ifp, "%s", &info[i].name);
		fgetc(ifp);		/* read space */
		fscanf(ifp, "%d", &info[i].age);
		fgetc(ifp);		/* read space */
		fscanf(ifp, "%lf", &info[i].gpa);
		gpa_sum += info[i].gpa; 
		i++;
		
	}
	
	/* print the average gpa of all students */
	printf("\nThe average gpa of all students is: %.2lf\n", gpa_sum / num_of_students);
	
	/* print names of students who have at least 2.0 gpa */
	printf("\nStudents who have at least 2.0 or greater: \n");
	for(i = 0; i < num_of_students; i++)
		if(info[i].gpa >= 2)
			printf("%s\n", info[i].name);
	
	/* sort names */
	for(i = 0; i < num_of_students; i++){
		for(j = 0; j < num_of_students - 1; j++){
			/* check if first letter uppercase or no */
			int boolean = strncmp(info[j].name, info[j + 1].name, 21);
			/* if index (j - 1) is less than index (j) than swap */
			if(boolean > 0){ 
			
				/* swap names */
				char temp_name[21];
				strcpy(temp_name, info[j].name);
				strcpy(info[j].name, info[j + 1].name);
				strcpy(info[j + 1].name, temp_name);
				
				/*swap age */
				int temp_age = info[j].age;
				info[j].age = info[j + 1].age;
				info[j + 1].age = temp_age;
				
				/* swap gpa */
				double temp_gpa = info[j].gpa;
				info[j].gpa = info[j + 1].gpa;
				info[j + 1].gpa = temp_gpa;
			
			}
		
		}
		
	}
	
	/* display sorted student info */
	printf("\nName\tAge\tGPA");
	for(i = 0; i < num_of_students; i++)
		printf("\n%s\t%d\t%.2lf", info[i].name, info[i].age, info[i].gpa);
	printf("\n\n");
	free(info);
	fclose(ifp);
	
 }
