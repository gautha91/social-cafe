/*
 * Copyright 2012 Facebook
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#import <FacebookSDK/FacebookSDK.h>
#import "MenuViewController.h"
#import "OrderViewController.h"
#import "Menu.h"

@interface MenuViewController () 
<UITableViewDataSource,
UITableViewDelegate,
MenuDataLoadDelegate>

@property (strong, nonatomic) IBOutlet FBProfilePictureView *userProfilePictureView;
@property (strong, nonatomic) IBOutlet UILabel *userNameLabel;
@property (weak, nonatomic) IBOutlet UITableView *menuTableView;
@property (strong, nonatomic) Menu *menu;
@property (strong, nonatomic) UILabel *label;
@property (strong, nonatomic) NSDictionary<FBGraphUser> *user;

- (void)populateUserDetails;
- (void)initMenuItems;

@end

@implementation MenuViewController
@synthesize userProfilePictureView;
@synthesize userNameLabel;
@synthesize menuTableView;
@synthesize menu = _menu;
@synthesize label = _label;
@synthesize user = _user;

#pragma mark - Helper methods
/*
 * This method personalizes the user's experience by getting the user's
 * name and profile picture.
 */
- (void)populateUserDetails {
    if (FBSession.activeSession.isOpen) {
        FBRequest *me = [FBRequest requestForMe];
        [me startWithCompletionHandler: ^(FBRequestConnection *connection, 
                                          NSDictionary<FBGraphUser> *my,
                                          NSError *error) {
            self.label.text = my.first_name;
        }];
        [[FBRequest requestForMe] startWithCompletionHandler:
         ^(FBRequestConnection *connection, NSDictionary<FBGraphUser> *user, NSError *error) {
             if (!error) {
                 self.userNameLabel.text = user.name;
                 self.userProfilePictureView.profileID = [user objectForKey:@"id"];
                 self.menu.profileID = [user objectForKey:@"id"];
                 self.user = user;
             }
         }];   
    }
}

/*
 * This method initializes the menu items
 */
- (void)initMenuItems {
    self.menu = [[Menu alloc] init];
    self.menu.delegate = self;
}


#pragma mark - View life cycle
- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [self initMenuItems];
}

- (void)viewDidUnload
{
    [self setUserProfilePictureView:nil];
    [self setUserNameLabel:nil];
    [self setMenuTableView:nil];
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];

    // Present login modal if necessary after the view has been
    // displayed, not in viewWillAppear: so as to allow display
    // stack to "unwind"
    if (FBSession.activeSession.state == FBSessionStateOpen ||
        FBSession.activeSession.state == FBSessionStateCreatedTokenLoaded) {
    } else {
        [self performSegueWithIdentifier:@"SegueToLogin" sender:self];
    }
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    if (FBSession.activeSession.state == FBSessionStateOpen) {
        // If the user's session is active personalize the
        // experience
        [self populateUserDetails];
    } else if (FBSession.activeSession.state == FBSessionStateCreatedTokenLoaded) {
        // If the session info is in cache, open the session but do not display
        // any login UX.
        NSArray *permissions = [NSArray arrayWithObjects:
                                @"publish_actions",
                                @"user_photos",
                                @"email",
                                nil];
        [FBSession openActiveSessionWithPermissions:permissions
                                       allowLoginUI:NO
                                  completionHandler:^(FBSession *session,
                                                      FBSessionState state,
                                                      NSError *error) {
            if (!error) {
                [self populateUserDetails];
            }
        }];
    }
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation = UIInterfaceOrientationPortrait);
}

#pragma mark - UITableViewDatasource and UITableViewDelegate Methods
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 80.0;
}

// Customize the number of sections in the table view.
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return [self.menu.items count];
}

// Customize the appearance of table view cells.
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView
                             dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle
                                      reuseIdentifier:CellIdentifier];
    }
    cell.imageView.image = [UIImage imageNamed:[[self.menu.items objectAtIndex:indexPath.row]
                                                objectForKey:@"picture"]];
    cell.textLabel.text = [[self.menu.items objectAtIndex:indexPath.row] objectForKey:@"title"];
    cell.textLabel.font = [UIFont fontWithName:@"Helvetica" size:20.0];
    if ([[self.menu.items objectAtIndex:indexPath.row] objectForKey:@"likeCount"] &&
        [[self.menu.items objectAtIndex:indexPath.row] objectForKey:@"orderCount"]) {
        int likePercentage =
        ([[[self.menu.items objectAtIndex:indexPath.row] objectForKey:@"likeCount"] doubleValue] /
         [[[self.menu.items objectAtIndex:indexPath.row] objectForKey:@"orderCount"] doubleValue]) * 100.0;
        cell.detailTextLabel.numberOfLines = 2;
        cell.detailTextLabel.text =
            [NSString stringWithFormat:@"%@ others enjoyed this.\n%d%% of orders enjoyed this.",
                                     [[self.menu.items
                                       objectAtIndex:indexPath.row]
                                      objectForKey:@"likeCount"],
                                     likePercentage];
    }

    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [self performSegueWithIdentifier:@"SegueToOrder" sender:self];
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    if ([segue.identifier isEqualToString:@"SegueToOrder"]) {
        // Set the data row
        OrderViewController *ovc = (OrderViewController *)segue.destinationViewController;
        ovc.menuItem = [self.menu.items objectAtIndex:[[self.menuTableView indexPathForSelectedRow] row]];
        ovc.user = self.user;
        [self.menuTableView deselectRowAtIndexPath:[self.menuTableView indexPathForSelectedRow] animated:NO];
    }
}

#pragma mark - Menu Data Load Delegate
/*
 * This reloads the menu if there are any changes to the menu data
 */
- (void)menu:(Menu *)menu didLoadData:(NSDictionary *)results index:(NSUInteger)index
{
    [self.menuTableView reloadData];
}

#pragma mark - Action methods
- (IBAction)logoutButtonClicked:(id)sender {
    [FBSession.activeSession closeAndClearTokenInformation];
    [self performSegueWithIdentifier:@"SegueToLogin" sender:self];
}


@end