using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using EpytomeUI.ViewModels;

namespace EpytomeUI.Controls
{
    public partial class LoginHeader : UserControl
    {
        private UserInfoViewModel _viewModel;
        public UserInfoViewModel ViewModel
        {
            get { return _viewModel; }
            set 
            {
                _viewModel = value;
                DataContext = value;
            }
        }

        public LoginHeader()
        {
            InitializeComponent();
        }
    }
}
