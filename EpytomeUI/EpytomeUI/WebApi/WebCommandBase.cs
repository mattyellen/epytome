using System;
using System.IO;
using System.Net;
using System.Runtime.Serialization;
using System.Runtime.Serialization.Json;
using System.Windows;
using System.Windows.Controls;
using EpytomeUI.Controls;
using EpytomeUI.Utilities;

namespace EpytomeUI.WebApi
{
    public class WebCommandBase<TRequest, TReply>
        where TRequest : RequestBase
        where TReply : ReplyBase
    {
        private readonly string _commandName;
        public Action<TReply> ReplyCallback { get; set; }
        public Action<ReplyBase> ErrorCallback { get; set; }

        public WebCommandBase(string commandName)
        {
            _commandName = commandName;
        }

        public void Send(TRequest request)
        {
            request.Command = _commandName;

            var stream = new MemoryStream();
            var jsonSerializer = new DataContractJsonSerializer(typeof(TRequest));
            jsonSerializer.WriteObject(stream, request);

            stream.Position = 0;
            var sr = new StreamReader(stream);
            var data = sr.ReadToEnd();

            var client = new WebClient();
            client.Headers[HttpRequestHeader.ContentType] = "text/plain";

            client.UploadStringCompleted += OnUploadStringCompleted;
            client.UploadStringAsync(new Uri(AppUtils.BaseUri + "/EpytomeServer"), data);
            MainPageViewModel.GlobalActivityStack.Push();
        }

        private void OnUploadStringCompleted(object sender, UploadStringCompletedEventArgs e)
        {
            if (e.Error != null)
            {
                throw e.Error;
            }
            var stream = new MemoryStream();
            var sw = new StreamWriter(stream);
            sw.Write(e.Result);
            sw.Flush();

            var response = new DataContractJsonSerializer(typeof(TReply));
            stream.Position = 0;
            var reply = (TReply)response.ReadObject(stream);
            reply.ShowErrorWindow = true;

            if (reply.Status == CommandStatus.Success)
            {
                if (ReplyCallback != null)
                {
                    ReplyCallback(reply);
                }
                MainPageViewModel.GlobalActivityStack.Pop();
            }
            else
            {
                MainPageViewModel.GlobalActivityStack.Pop();
                if (ErrorCallback != null)
                {
                    ErrorCallback(reply);
                }

                if (reply.ShowErrorWindow)
                {
                    var error = new ErrorWindow
                                    {
                                        ErrorTitle = {Text = reply.Status.ToString()},
                                        ErrorDetail = {Text = reply.StatusDetail ?? "No Details"}
                                    };
                    error.Show();
                }
            }
        }
    }

    public enum CommandStatus
    {
        Invalid,
        Success,
        Failure,
        UserNotLoggedIn,
        DuplicateGame,
        LockFailure,
        DuplicateNickname,
        ConcurrentAction,
        UserInWorld,
        AssertionFailed,
        InsufficientActionPoints,
        CharacterNotFound,
        NoConstruction,
        InvalidConstructionType,
        NoResources,
        CharacterInventoryFull,
        ItemNotInInventory
    }

    public static class EnumVal<T> where T : struct
    {
        public static T Get(string strVal)
        {
            T result;
            if (Enum.TryParse(strVal, out result))
            {
                return result;
            }
            throw new Exception("Failed to deserialize enum: " + strVal);
        }
    }

    [DataContract]
    public class ReplyBase
    {
        [DataMember] public CommandStatus Status { get; set; }
        [DataMember] public string StatusDetail { get; set; }

        public bool ShowErrorWindow { get; set; }
    }

    [DataContract]
    public class RequestBase
    {
        [DataMember] public string Command { get; set; }
    }
}
